package fr.kamael.skylandersfight.skylanders.vie.listener;

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
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;

public class VieListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerInteractVie(PlayerInteractEvent event) {
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

            if (skylander instanceof StealthElf) {
            	handleStealthElf((StealthElf) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(VieListener, playerInteractVie) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleStealthElf(StealthElf skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(StealthElf.nameFirstSpell, skylander::firstSpell_Invi);
        actions.put(StealthElf.nameSecondSpell, skylander::secondSpell_Dash);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
