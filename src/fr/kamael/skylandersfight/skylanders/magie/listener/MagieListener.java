package fr.kamael.skylandersfight.skylanders.magie.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.skylanders.magie.StarStrike;
import fr.kamael.skylandersfight.skylanders.magie.Voodood;

public class MagieListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void starstrikeProjectileHit(ProjectileHitEvent event) {
		try {
			if (plugin.game == null || !plugin.game.isState(GameState.FIGHTING) || !(event.getEntity() instanceof Snowball) || !(event.getEntity().getShooter() instanceof Player))
				return;

			Projectile projectile = event.getEntity();
            Player player = (Player) projectile.getShooter();
            Skylander skylander = plugin.game.getPlayer(player).getSkylander();
            
            if (skylander instanceof StarStrike && skylander.isAlive()) {
                if (event.getHitEntity() instanceof Player) {
                	player.getInventory().addItem(StarStrike.getItemWeapon(1));
                } else {
                    new BukkitRunnable() {                    	
                        @Override
                        public void run() {
                        	player.getInventory().addItem(StarStrike.getItemWeapon(1));
                        	cancel();
                        	return;
                        }
                    }.runTaskLater(plugin, StarStrike.delayMissPassif * 20);
                }
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(MagieListener, playerInteractMagie) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerInteractMagie(PlayerInteractEvent event) {
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

            if (skylander instanceof Spyro) {
            	handleSpyro((Spyro) skylander, action, nameItem);
            } else if (skylander instanceof Voodood) {
            	handleVoodood((Voodood) skylander, action, nameItem);
            } else if (skylander instanceof StarStrike) {
            	handleStarStrike((StarStrike) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(MagieListener, playerInteractMagie) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleSpyro(Spyro skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Spyro.nameFirstSpell, skylander::firstSpell_Dash);
        actions.put(Spyro.nameSecondSpell, skylander::secondSpell_Fly);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleVoodood(Voodood skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Voodood.nameFirstSpell, skylander::firstSpell_Stun);
        actions.put(Voodood.nameSecondSpell, skylander::secondSpell_Teleportation);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	
	
	private void handleStarStrike(StarStrike skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(StarStrike.nameFirstSpell, skylander::firstSpell_Meteor);
        actions.put(StarStrike.nameSecondSpell, skylander::secondSpell_Invul);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
