/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package autosaveworld.commands;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import autosaveworld.config.AutoSaveConfig;
import autosaveworld.config.AutoSaveConfigMSG;
import autosaveworld.config.LocaleChanger;
import autosaveworld.core.AutoSaveWorld;

public class CommandsHandler implements CommandExecutor {

	private AutoSaveWorld plugin = null;
	private AutoSaveConfig config;
	private AutoSaveConfigMSG configmsg;
	private LocaleChanger localeChanger;
	public CommandsHandler(AutoSaveWorld plugin, AutoSaveConfig config,
			AutoSaveConfigMSG configmsg, LocaleChanger localeChanger) {
		this.plugin = plugin;
		this.config = config;
		this.configmsg = configmsg;
		this.localeChanger = localeChanger;
	};
	
	private PermissionCheck permCheck = new PermissionCheck();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		
		String commandName = command.getName().toLowerCase();

		//check permissions
		if (!permCheck.isAllowed(sender, commandName, args, config.commandonlyfromconsole)) {
			plugin.sendMessage(sender, configmsg.messageInsufficientPermissions);
			return true;
		}
		
		// now handle commands
		if (commandName.equalsIgnoreCase("autosave")) {
			//"autosave" command handler
			plugin.saveThread.startsave();
			return true;
		} else if (commandName.equalsIgnoreCase("autobackup")) {
			//"autobackup" command handler
			plugin.backupThread6.startbackup();
			return true;
		} else if (commandName.equalsIgnoreCase("autopurge")) {
			//"autopurge" command handler
			plugin.purgeThread.startpurge();
			return true;
		} else if (commandName.equalsIgnoreCase("autosaveworld")) {
			//"autosaveworld" command handler
			if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				// help
				plugin.sendMessage(sender, "&f/asw help&7 - &3Показывает все команды");
				plugin.sendMessage(sender, "&f/asw serverstatus&7 - &3Показывает CPU,RAM,HDD загрузку");
				plugin.sendMessage(sender, "&f/asw forcegc&7 - &3Начинает сборку мусора");
				plugin.sendMessage(sender, "&f/asw pmanager load {pluginname}&7 - &3Загружает плагин {pluginname}");
				plugin.sendMessage(sender, "&f/asw pmanager unload {pluginname}&7 - &3Выгружает плагин {pluginname}");
				plugin.sendMessage(sender, "&f/asw pmanager reload {pluginname}&7 - &3Перезагружает (unload затем load) плагин {pluginname}");
				plugin.sendMessage(sender, "&f/asw save&7 - &3Сохраняет все миры и игроков");
				plugin.sendMessage(sender, "&f/save&7 - &3То же, что /asw save");
				plugin.sendMessage(sender, "&f/asw backup&7 - &3Бекапит миры из config.yml (* - все миры) и плагиы (если указаны)");
				plugin.sendMessage(sender, "&f/backup&7 - &3То же, что /asw backup");
				plugin.sendMessage(sender, "&f/asw purge&7 - &3Отчищает информацию из плагинов о неактивных игроках");
				plugin.sendMessage(sender, "&f/purge&7 - &3То же, что /asw purge");
				plugin.sendMessage(sender, "&f/asw restart&7 - &3Перезагружает сервер");
				plugin.sendMessage(sender, "&f/asw regenworld {world}&7 - &3Регенерирует мир");
				plugin.sendMessage(sender, "&f/asw reload&7 - &3Перезагружает все конфиги :)");
				plugin.sendMessage(sender, "&f/asw reloadconfig&7 - &3Перезагружает (config.yml)");
				plugin.sendMessage(sender, "&f/asw reloadmsg&7 - &3Перезагружает (configmsg.yml)");
				plugin.sendMessage(sender, "&f/asw locale&7 - &3Показывает текущий язык плагина");
				plugin.sendMessage(sender, "&f/asw locale available&7 - &3Показывает доступные языки");
				plugin.sendMessage(sender, "&f/asw locale load {locale}&7 - &3Устанавливает язык плагина");
				plugin.sendMessage(sender, "&f/asw info&7 - &3Немного информации о плагине");
				plugin.sendMessage(sender, "&f/asw version&7 - &3Показывает версию плагина");
				return true;
			} else if (args.length >= 3 && args[0].equalsIgnoreCase("pmanager")) {
				String[] nameArray = Arrays.copyOfRange(args, 2, args.length);
				StringBuilder sb = new StringBuilder(50);
				for (String namearg : nameArray)
				{
					sb.append(namearg);
					sb.append(" ");
				}
				sb.deleteCharAt(sb.length()-1);
				plugin.pmanager.handlePluginManagerCommand(sender, args[1], sb.toString());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("forcegc")) {
				plugin.sendMessage(sender, "&9Наинаю GC");
				System.gc();
				System.gc();
				plugin.sendMessage(sender, "&9GC вероятно завершено");
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("serverstatus")) {
				DecimalFormat df = new DecimalFormat("0.00");
				//processor (if available)
				try {
					com.sun.management.OperatingSystemMXBean systemBean = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
					double cpuusage = systemBean.getProcessCpuLoad()*100;
					if (cpuusage > 0) {
						sender.sendMessage(ChatColor.GOLD+"CPU: "+ChatColor.RED+df.format(cpuusage)+"%");
					} else {
						sender.sendMessage(ChatColor.GOLD+"CPU: "+ChatColor.RED+"not available");
					}
				} catch (Exception e) {}
				//memory
				Runtime runtime = Runtime.getRuntime();
				long maxmemmb = runtime.maxMemory()/1024/1024;
				long freememmb = (runtime.maxMemory()-(runtime.totalMemory()-runtime.freeMemory()))/1024/1024;
				sender.sendMessage(ChatColor.GOLD+"RAM: "+ChatColor.RED+df.format((maxmemmb-freememmb)*100/maxmemmb)+"% "+ChatColor.DARK_AQUA+"("+ChatColor.DARK_GREEN+(maxmemmb-freememmb)+"/"+maxmemmb+" MB"+ChatColor.DARK_AQUA+")"+ChatColor.RESET);
				//hard drive
				File file = new File(".");
				long maxspacegb = file.getTotalSpace()/1024/1024/1024;
				long freespacegb = file.getFreeSpace()/1024/1024/1024;
				sender.sendMessage(ChatColor.GOLD+"HDD: "+ChatColor.RED+df.format((maxspacegb-freespacegb)*100/maxspacegb)+"% "+ChatColor.DARK_AQUA+"("+ChatColor.DARK_GREEN+(maxspacegb-freespacegb)+"/"+maxspacegb+" GB"+ChatColor.DARK_AQUA+")"+ChatColor.RESET);
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
				//save
				plugin.saveThread.startsave();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("backup")) {
				//backup
				plugin.backupThread6.startbackup();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("purge")) {
				//purge
				plugin.purgeThread.startpurge();
				return true;
			} else if ((args.length == 1 && args[0].equalsIgnoreCase("restart"))) {
				//restart
				plugin.autorestartThread.startrestart(false);
				return true;
			} else if ((args.length == 2 && args[0].equalsIgnoreCase("regenworld"))) {
				//regen world
				if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
					plugin.sendMessage(sender, "[AutoSaveWorld] Для этого, необходимо установить WorldEdit");
					return true;
				}
				if (Bukkit.getWorld(args[1]) == null) {
					plugin.sendMessage(sender, "[AutoSaveWorld] Этот мир не доступен");
					return true;
				}
				if (plugin.worldregenInProcess) {
					plugin.sendMessage(sender, "[AutoSaveWorld] Пожалуйста подождите пока текущая регенерация завершится");
					return true;
				}
				plugin.worldregencopyThread.startworldregen(args[1]);
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				//reload
				config.load();
				configmsg.loadmsg();
				plugin.sendMessage(sender, "[AutoSaveWorld] Все конфигурации перезагружены");
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reloadconfig")) {
				//reload config
				config.load();
				plugin.sendMessage(sender,"[AutoSaveWorld] Главная конфигурация перезагружена");
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reloadmsg")) {
				//reload messages
				configmsg.loadmsg();
				plugin.sendMessage(sender, "[AutoSaveWorld] Файл сообщений перезагружен");
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
				//version
				plugin.sendMessage(sender, plugin.getDescription().getName()+ " " + plugin.getDescription().getVersion());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				//info
				plugin.sendMessage(sender,"&9======AutoSaveWorld Инфа & Статус======");
				if (config.saveEnabled) {
					plugin.sendMessage(sender, "&2AutoSave активирован");
					plugin.sendMessage(sender, "&2Посл. сохранение: " + plugin.LastSave);
				} else {
					plugin.sendMessage(sender, "&2AutoSave не активирован");
				}
				if (config.backupEnabled) {
					plugin.sendMessage(sender, "&2AutoBackup активирован");
					plugin.sendMessage(sender, "&2Посл. бекап: " + plugin.LastBackup);
				} else {
					plugin.sendMessage(sender, "&2AutoBackup не активирован");
				}
				plugin.sendMessage(sender,"&9====================================");
				return true;
			} else if ((args.length >= 1 && args[0].equalsIgnoreCase("locale"))) {
				//locale loader
				if (args.length == 2 && args[1].equalsIgnoreCase("available")) {
					plugin.sendMessage(sender, "Доступные языки: "+ localeChanger.getAvailableLocales());
					return true;
				} else if (args.length == 2 && args[1].equalsIgnoreCase("load")) {
					plugin.sendMessage(sender,"Вам следует выбрать язык для загрузки");
					return true;
				} else if (args.length == 3 && args[1].equalsIgnoreCase("load")) {
					if (localeChanger.getAvailableLocales().contains(args[2])) {
						plugin.sendMessage(sender, "Загрузка языка " + args[2]);
						localeChanger.loadLocale(args[2]);
						plugin.sendMessage(sender, "Загрузка языка " + args[2]);
						return true;
					} else {
						plugin.sendMessage(sender, "Язык " + args[2] + " не доступен");
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}
}
