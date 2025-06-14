package fr.kamael.skylandersfight.skylanders.bogda.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.bogda.Cyroule;

public class BogdaListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void inventoryBogdaClick(InventoryClickEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {				
				Player player = (Player) event.getWhoClicked();
				Skylander skylander = plugin.game.getPlayer(player).getSkylander();
				ItemStack it = event.getCurrentItem();
				
				if (it == null || it.getType().equals(Material.GRAY_STAINED_GLASS_PANE))
					return;

				if (event.getView().getTitle().equalsIgnoreCase(Cyroule.namePassif) && skylander instanceof Cyroule) {
					event.setCancelled(true);
					
					SkullMeta itM = (SkullMeta) it.getItemMeta();
					((Cyroule) skylander).passif_Apply(itM.getOwningPlayer().getPlayer());
					
					player.closeInventory();
					return;
				}
				
				return;	
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, inventoryArenaClick) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerInteractBogda(PlayerInteractEvent event) {
		try {
			if (plugin.game == null || !plugin.game.isState(GameState.FIGHTING))
				return;
			
            Player player = event.getPlayer();
            Action action = event.getAction();
            ItemStack item = event.getItem();
            Skylander skylander = plugin.game.getPlayer(player).getSkylander();
            
            if (skylander.checkStatus(Status.NOSPELL) || item == null || item.getItemMeta() == null)
                return;
            
            String nameItem = item.getItemMeta().getDisplayName();

            if (skylander instanceof Cyroule) {
            	handleCyroule((Cyroule) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(BogdaListener, playerInteractBogda) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleCyroule(Cyroule skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Cyroule.namePassif, skylander::passif_Inventory);
        actions.put(Cyroule.nameFirstSpell, skylander::firstSpell_NoAttack);
        actions.put(Cyroule.nameSecondSpell, skylander::secondSpell_Arena);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
