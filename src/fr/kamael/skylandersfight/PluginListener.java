package fr.kamael.skylandersfight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PluginListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		try {
			Player player = event.getPlayer();
			player.setSaturation(999999999);
			player.setFoodLevel(20);
			
			String msg = Constants.prefixMessage + "→ §b" + player.getName() + " §f" + Constants.welcomeMessage.get(plugin.random.nextInt(Constants.welcomeMessage.size()));
			
			event.setJoinMessage(msg);
		} 
		catch(Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, playerJoin) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		try {
			Player player = event.getPlayer();
			
			String msg = Constants.prefixMessage + "← §b" + player.getName() + " §f" + Constants.goodbyeMessage.get(plugin.random.nextInt(Constants.goodbyeMessage.size()));
			
			event.setQuitMessage(msg);
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, playerQuit) : §7"+e.getMessage());	
			return;
		}
	}
}
