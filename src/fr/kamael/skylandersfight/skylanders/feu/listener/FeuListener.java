package fr.kamael.skylandersfight.skylanders.feu.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.skylanders.feu.Flameslinger;
import fr.kamael.skylandersfight.skylanders.feu.Smolderdash;
import fr.kamael.skylandersfight.skylanders.feu.Sunburn;

public class FeuListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerInteractFeu(PlayerInteractEvent event) {
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

            if (skylander instanceof Eruptor) {
            	handleEruptor((Eruptor) skylander, action, nameItem);
            } else if (skylander instanceof Flameslinger) {
            	handleFlameslinger((Flameslinger) skylander, action, nameItem);
            } else if (skylander instanceof Sunburn) {
            	handleSunburn((Sunburn) skylander, action, nameItem);
            } else if (skylander instanceof Smolderdash) {
            	handleSmolderdash((Smolderdash) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(FeuListener, playerInteractFeu) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleEruptor(Eruptor skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Eruptor.nameFirstSpell, skylander::firstSpell_Fireball);
        actions.put(Eruptor.nameSecondSpell, skylander::secondSpell_Lava);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleFlameslinger(Flameslinger skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Flameslinger.nameFirstSpell, skylander::firstSpell_FireArrow);
        actions.put(Flameslinger.nameSecondSpell, skylander::secondSpell_Run);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleSunburn(Sunburn skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Sunburn.nameFirstSpell, skylander::firstSpell_Fire);
        actions.put(Sunburn.nameSecondSpell, skylander::secondSpell_Teleportation);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
	private void handleSmolderdash(Smolderdash skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Smolderdash.nameFirstSpell, skylander::firstSpell_Fireball);
        actions.put(Smolderdash.nameSecondSpell, skylander::secondSpell_Powerup);

        if (isRightClick(action) && actions.containsKey(name))
        	actions.get(name).run();
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
    
    @EventHandler
    public void smolderdashFish(PlayerFishEvent event) {
        try {
            if (plugin.game == null || !plugin.game.isState(GameState.FIGHTING))
                return;
            if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND) {
                Location hookLocation = event.getHook().getLocation();
                Player player = event.getPlayer();
                Skylander skylander = plugin.game.getPlayer(player).getSkylander();
                
                if (skylander instanceof Smolderdash)
                	((Smolderdash) skylander).passif_Jump(hookLocation);
            } 
        } catch(Exception e ) {
            Bukkit.broadcastMessage(Constants.prefixError + "(FeuListener, smolderdashFish) : §7" + e.getMessage());
            return;
        }
    }

}
