package fr.kamael.skylandersfight.skylanders.air.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.air.JetVac;
import fr.kamael.skylandersfight.skylanders.air.LightningRod;

public class AirListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void playerInteractAir(PlayerInteractEvent event) {
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

            if (skylander instanceof LightningRod) {
            	handleLightningRod((LightningRod) skylander, action, nameItem);
            } else if (skylander instanceof JetVac) {
            	handleJetVac((JetVac) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(AirListener, playerInteractAir) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleLightningRod(LightningRod skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(LightningRod.nameFirstSpell, skylander::firstSpell_Stun);
        actions.put(LightningRod.nameSecondSpell, skylander::secondSpell_Fly);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleJetVac(JetVac skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(JetVac.nameFirstSpell, skylander::firstSpell_Damage);
        actions.put(JetVac.nameSecondSpell, skylander::secondSpell_Tornado);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
    
    @EventHandler
    public void jetvacDoubleJump(PlayerToggleFlightEvent event) {
		try {
			if (plugin.game == null || !plugin.game.isState(GameState.FIGHTING))
				return;
			
			Player player = event.getPlayer();
			
	        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
	            return;
	        
	        Skylander skylander = plugin.game.getPlayer(player).getSkylander();
	        
	        if (skylander.isAlive() && skylander instanceof JetVac) {
	        	((JetVac) skylander).passif_DoubleJump();
	        }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(AirListener, playerInteractAir) : §7"+e.getMessage());	
			return;
		}
    }
}
