package fr.kamael.skylandersfight.skylanders.mort.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.mort.GhostRoaster;
import fr.kamael.skylandersfight.skylanders.mort.GrimCreeper;

public class MortListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerInteractMort(PlayerInteractEvent event) {
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

            if (skylander instanceof GhostRoaster) {
            	handleGhostRoaster((GhostRoaster) skylander, action, nameItem);
            } else if (skylander instanceof GrimCreeper) {
            	handleGrimCreeper((GrimCreeper) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(MortListener, playerInteractMort) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleGhostRoaster(GhostRoaster skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(GhostRoaster.nameFirstSpell, skylander::firstSpell_Invul);
        actions.put(GhostRoaster.nameSecondSpell, skylander::secondSpell_Spectral);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleGrimCreeper(GrimCreeper skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(GrimCreeper.nameFirstSpell, skylander::firstSpell_Dash);
        actions.put(GrimCreeper.nameSecondSpell, skylander::secondSpell_Separation);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

}
