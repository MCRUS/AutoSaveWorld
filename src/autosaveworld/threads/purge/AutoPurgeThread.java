/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package autosaveworld.threads.purge;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;

import autosaveworld.config.AutoSaveConfig;
import autosaveworld.config.AutoSaveConfigMSG;
import autosaveworld.core.AutoSaveWorld;

public class AutoPurgeThread extends Thread {

	private AutoSaveWorld plugin = null;
	private AutoSaveConfig config;
	private AutoSaveConfigMSG configmsg;
	public AutoPurgeThread(AutoSaveWorld plugin, AutoSaveConfig config,
			AutoSaveConfigMSG configmsg) {
		this.plugin = plugin;
		this.config = config;
		this.configmsg = configmsg;
	}


	public void stopThread() {
		this.run = false;
	}

	public void startpurge() {
		if (plugin.purgeInProgress) {
			plugin.warn("Multiple concurrent purges attempted! Purge interval is likely too short!");
			return;
		}
		command = true;
	}

	// The code to run...weee
	private volatile boolean run = true;
	private boolean command = false;
	public void run() {

		plugin.debug("AutoPurgeThread Started");
		Thread.currentThread().setName("AutoSaveWorld AutoPurgeThread");

		
		while (run) {
			// Prevent AutoPurge from never sleeping
			// If interval is 0, sleep for 10 seconds and skip purging
			if (config.purgeInterval == 0) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
				continue;
			}

			// Do our Sleep stuff!
			for (int i = 0; i < config.purgeInterval; i++) {
				if (!run) {break;}
				if (command) {break;}
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}

			if (run&&(config.purgeEnabled || command)) {performPurge();}

		}
		
		plugin.debug("Graceful quit of AutoPurgeThread");
		
	}




	public void performPurge() {
		
		command = false;

		if (plugin.backupInProgress) {
			plugin.warn("AutoBackup is in progress. Purge cancelled.");
			return;
		}
		if (plugin.worldregenInProcess) {
			plugin.warn("WorldRegen is in progress. Purge cancelled.");
			return;
		}
			
			plugin.purgeInProgress = true;
			
			plugin.broadcast(configmsg.messagePurgeBroadcastPre, config.purgeBroadcast);
			
			long awaytime = config.purgeAwayTime * 1000;
			
			plugin.debug("Purge started");
			
			plugin.debug("Gathering active players list");
			ActivePlayersList aplist = new ActivePlayersList();
			try {
				aplist.gatherActivePlayersList(awaytime);
				plugin.debug("Found "+aplist.getActivePlayersCount()+" active players");
			} catch (Exception e) {
				e.printStackTrace();
				plugin.debug("Failed to gather active players list, autopurge cancelled");
				plugin.broadcast(ChatColor.RED+"Failed to gather active players list, autopurge cancelled", config.purgeBroadcast);
				plugin.purgeInProgress = false;
				return;
			}
			
			PluginManager pm = plugin.getServer().getPluginManager();
			
			if ((pm.getPlugin("WorldGuard") != null)
					&& config.purgewg) {
				plugin.debug("WG found, purging");
				try {
					new WGPurge(plugin).doWGPurgeTask(aplist, config.purgewgregenrg, config.purgewgnoregenoverlap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if ((pm.getPlugin("LWC") != null)
					&& config.purgelwc) {
				plugin.debug("LWC found, purging");
				try {
					new LWCPurge(plugin).doLWCPurgeTask(aplist, config.purgelwcdelprotectedblocks);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if ((pm.getPlugin("Multiverse-Inventories") !=null) 
					&& config.purgemvinv ) {
				plugin.debug("Multiverse-Inventories found, purging");
				try {
					new MVInvPurge(plugin).doMVInvPurgeTask(aplist);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if ((pm.getPlugin("PlotMe") !=null) 
					&& config.purgepm) {
				plugin.debug("PlotMe found, purging");
				try {
					new PlotMePurge(plugin).doPlotMePurgeTask(aplist, config.purgepmregen);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if ((pm.getPlugin("Residence") !=null) 
					&& config.purgeresidence) {
				plugin.debug("Residence found, purging");
				try {
					new ResidencePurge(plugin).doResidencePurgeTask(aplist, config.purgeresregenarena);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (pm.getPlugin("Vault") != null) {
				VaultPurge vp = new VaultPurge(plugin);
				if (config.purgeeconomy) {
					plugin.debug("Vault found, purging economy");
					vp.doEconomyPurgeTask(aplist);
				}
				if (config.purgeperms) {
					plugin.debug("Vault found, purging permissions");
					vp.doPermissionsPurgeTask(aplist);
				}
			}
			
			
			plugin.debug("Purging player .dat files");
			if (config.purgedat) {
				try {
					new DatfilePurge(plugin).doDelPlayerDatFileTask(aplist);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			

			
			plugin.debug("Purge finished");
			

			plugin.broadcast(configmsg.messagePurgeBroadcastPost, config.purgeBroadcast);
			
			plugin.purgeInProgress = false;
			
	}


}
