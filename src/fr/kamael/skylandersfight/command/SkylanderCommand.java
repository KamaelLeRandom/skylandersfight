package fr.kamael.skylandersfight.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;

public class SkylanderCommand implements CommandExecutor {
	private Plugin plugin = Plugin.plugin;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				if (cmd.getName().equalsIgnoreCase("skylander") || cmd.getName().equalsIgnoreCase("s")) {
					if (args.length == 1) {
						
						// < /skylander help >
						if (args[0].equalsIgnoreCase("help")) {
							String message = "§6§l===== Informations Skylander =====" + '\n' +
					                 "§eListe des commandes disponibles : " + '\n' +
					                 "§b- §f/skylander game §7→ Gérer les paramètres du jeu" + '\n' +
					                 "§b- §f/skylander config §7→ Configurer le plugin" + '\n' +
					                 "§6§l===============================";
							player.sendMessage(message);
							player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
							return true;
						}
						
						// < /skylander config >
						if (args[0].equalsIgnoreCase("config")) {
							if (plugin.game == null) {
								player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
								player.sendMessage(Constants.prefixMessage + "Vous devez d'abord créer une partie.");
								return false;
							}
							player.openInventory(plugin.game.getConfig().getInventory());
							return true;
						}
						
						// < /skylander game >
						if (args[0].equalsIgnoreCase("game")) {
							
							return true;
						}
					}
				}
			}
			return false;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(SkylanderCommand, onCommand) : §7"+e.getMessage());	
			return false;
		}
	}
}
