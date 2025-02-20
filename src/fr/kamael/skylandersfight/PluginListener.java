package fr.kamael.skylandersfight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
	
	@EventHandler
	public void inventoryConfigClick(InventoryClickEvent event) {
		try {
			ItemStack it = event.getCurrentItem();
			InventoryAction action = event.getAction();
			
			if (it == null || it.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
				event.setCancelled(true);
				return;
			}
			
			if (event.getView().getTitle().equalsIgnoreCase(Constants.inventoryConfigName)) {
				event.setCancelled(true);
								
				switch (it.getItemMeta().getDisplayName()) {
					case "§aCommencer": {
						plugin.game.start();
						break;
					}
					case "§7Nombre de point": {
						it.setAmount(plugin.game.getConfig().updateNbPointWin(action)); 
						break;
					}
					case "§7Nombre de ligne de Skylanders" : {
						it.setAmount(plugin.game.getConfig().updateNbSkylanderLine(action));
						break;
					}
					case "§7Nombre de barre de vie": {
						it.setAmount(plugin.game.getConfig().updateNbLifebar(action));
						break;
					}
					case "§7Durée avant le Deathmatch": {
						it.setAmount(plugin.game.getConfig().updateTimerDM(action));
						break;
					}
					case "§7Équipes": {
						plugin.game.getConfig().updateActiveTeam(it);
						break;
					}
					case "§7Bloc de soin": {
						plugin.game.getConfig().updateActiveHeal(it);
						break;
					}
					case "§7Deathmatch": {
						plugin.game.getConfig().updateActiveDeathmatch(it);
						break;
					}
					case "§7Objets Aléatoire": {
						plugin.game.getConfig().updateActiveItem(it);
						break;
					}
					case "§7Bonus élémentaire": {
						plugin.game.getConfig().updateActiveBonusMap(it);
						break;
					}
					case "§7Événement d'Arène": {
						plugin.game.getConfig().updateActiveEventMap(it);
						break;
					}
					default:
						break;
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, inventoryConfigClick) : §7"+e.getMessage());	
			return;
		}
	}
}
