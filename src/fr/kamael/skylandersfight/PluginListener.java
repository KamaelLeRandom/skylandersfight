package fr.kamael.skylandersfight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;

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
	
	@EventHandler
	public void inventorySkylanderClick(InventoryClickEvent event) {
		try {
			event.setCancelled(true);
			
			ItemStack it = event.getCurrentItem();
			
			if (event.getView().getTitle().equalsIgnoreCase(Constants.inventorySkylanderName)) {
				if (it == null || it.getType().equals(Material.GRAY_STAINED_GLASS_PANE) || it.getType().equals(Material.BARRIER)) {
					return;
				}
				
				Player player = (Player) event.getWhoClicked();
				GamePlayer gamePlayer = plugin.game.getPlayer(player);
				String nameSkylander = it.getItemMeta().getDisplayName().substring(2);
				
				if (it.getType().equals(Material.COMPASS)) {
					// TODO : Choix aléatoire.
				}
				
				gamePlayer.setSkylander(SkylanderConverter.convert(nameSkylander, player));
				gamePlayer.getSkylander().sendDescription();
				player.closeInventory();
				return;
			}
			return;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, inventorySkylanderClick) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void inventoryArenaClick(InventoryClickEvent event) {
		try {
			event.setCancelled(true);
			
			ItemStack it = event.getCurrentItem();
			
			if (event.getView().getTitle().equalsIgnoreCase(Constants.inventoryArenaName)) {
				if (it == null || it.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
					return;
				}
				
				Player player = (Player) event.getWhoClicked();
				GamePlayer gamePlayer = plugin.game.getPlayer(player);
				gamePlayer.setVotedArena(it.getItemMeta().getDisplayName());
				player.closeInventory();
				return;
			}
			return;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, inventoryArenaClick) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.CHOOSING)) {
				
				Player player = (Player) event.getPlayer();
				GamePlayer gamePlayer = plugin.game.getPlayer(player);
				Boolean reopen = false;
				
				if (
					(event.getView().getTitle().equals(Constants.inventorySkylanderName) && gamePlayer.getSkylander() == null) ||
					(event.getView().getTitle().equals(Constants.inventoryArenaName) && gamePlayer.getVotedArena() == null)
				) {
					reopen = true;
				}
				
				if (reopen) {
        			new BukkitRunnable() {
						@Override
						public void run() {
							player.openInventory(event.getInventory());
							cancel();
							return;
						}
					}.runTaskTimer(plugin, 0, 10);
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage("§c[Error]§f (PluginListener, inventoryClose) : §7"+e.getMessage());	
		}
    }
}
