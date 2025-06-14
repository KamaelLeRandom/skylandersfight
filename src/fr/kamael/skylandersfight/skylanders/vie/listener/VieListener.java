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
import fr.kamael.skylandersfight.skylanders.vie.Camo;
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;
import fr.kamael.skylandersfight.skylanders.vie.StumpSmash;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;

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
            } else if (skylander instanceof Camo) {
            	handleCamo((Camo) skylander, action, nameItem);
            } else if (skylander instanceof StumpSmash) {
            	handleStumpSmash((StumpSmash) skylander, action, nameItem);
            } else if (skylander instanceof ZooLou) {
            	handleZooLou((ZooLou) skylander, action, nameItem);
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

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleCamo(Camo skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Camo.nameFirstSpell, skylander::firstSpell_Poison);
        actions.put(Camo.nameSecondSpell, skylander::secondSpell_Explosion);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleStumpSmash(StumpSmash skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(StumpSmash.namePassif, skylander::passif_Heal);
        actions.put(StumpSmash.nameFirstSpell, skylander::firstSpell_Damage);
        actions.put(StumpSmash.nameSecondSpell, skylander::secondSpell_Poison);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleZooLou(ZooLou skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(ZooLou.nameWeapon, skylander::passif_Chicken);
        actions.put(ZooLou.nameFirstSpell, skylander::firstSpell_Wolf);
        actions.put(ZooLou.nameSecondSpell, skylander::secondSpell_Pig);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
