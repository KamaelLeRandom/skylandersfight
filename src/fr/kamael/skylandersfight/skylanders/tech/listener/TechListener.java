package fr.kamael.skylandersfight.skylanders.tech.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.tech.Boomer;
import fr.kamael.skylandersfight.skylanders.tech.Drobot;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.skylanders.tech.TriggerHappy;

public class TechListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerInteractTech(PlayerInteractEvent event) {
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

            if (skylander instanceof TriggerHappy) {
            	handleTriggerHappy((TriggerHappy) skylander, action, nameItem);
            } else if (skylander instanceof Drobot) {
            	handleDrobot((Drobot) skylander, action, nameItem);
            } else if (skylander instanceof Boomer) {
            	handleBoomer((Boomer) skylander, action, nameItem);
            } else if (skylander instanceof Sprocket) {
            	handleSprocket((Sprocket) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(TechListener, playerInteractTech) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleTriggerHappy(TriggerHappy skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(TriggerHappy.namePassif, skylander::passif_Gold);
        actions.put(TriggerHappy.nameFirstSpell, skylander::firstSpell_Arrow);
        actions.put(TriggerHappy.nameSecondSpell, skylander::secondSpell_Stun);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleDrobot(Drobot skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Drobot.nameFirstSpell, skylander::firstSpell_Laser);
        actions.put(Drobot.nameSecondSpell, skylander::secondSpell_Fly);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleBoomer(Boomer skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Boomer.nameWeapon, skylander::passif_ThrowTNT);
        actions.put(Boomer.nameFirstSpell, skylander::firstSpell_Trap);
        actions.put(Boomer.nameSecondSpell, skylander::secondSpell_Explosion);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleSprocket(Sprocket skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Sprocket.nameWeapon, skylander::passif_Teleport);
        actions.put(Sprocket.nameFirstSpell, skylander::firstSpell_Minecart);
        actions.put(Sprocket.nameSecondSpell, skylander::secondSpell_Inventory);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
    
	@EventHandler
	public void inventoryClickTech(InventoryClickEvent event) {
		try {
			if (event.getCurrentItem() == null || plugin.game == null || !plugin.game.isState(GameState.FIGHTING))
				return;
			
			Player player = (Player) event.getWhoClicked();
			Skylander skylander = plugin.game.getPlayer(player).getSkylander();
			ItemStack it = event.getCurrentItem();

			if (event.getView().getTitle().equalsIgnoreCase(Sprocket.nameSecondSpell) && skylander instanceof Sprocket) {
				event.setCancelled(true);
				
				((Sprocket) skylander).secondSpell_Build(it);
				
				player.closeInventory();
				return;
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage("§c[Error]§f (TechListener, inventoryClickTech) : §7"+e.getMessage());	
		}
	}
}
