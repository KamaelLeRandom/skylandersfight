package fr.kamael.skylandersfight.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class ArenaListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void projectileLauch(ProjectileLaunchEvent event) {
		try {
			if (plugin != null && 
				plugin.game.isState(GameState.FIGHTING) && 
				plugin.game.getRound().getArena() instanceof ParadisBlanc &&
				event.getEntity() instanceof Snowball) 
			{
				Snowball snowball = (Snowball) event.getEntity();
				Player player = (Player) snowball.getShooter();
				Skylander skylander = plugin.game.getPlayer(player).getSkylander();
				ParadisBlanc arena = (ParadisBlanc) plugin.game.getRound().getArena();
				
				if (snowball.getItem().getItemMeta().getDisplayName().equals(ParadisBlanc.nameSnowball)) {
					new BukkitRunnable() {
						
						@Override
						public void run() {
							Location snowballLoc = snowball.getLocation();
							
							if (snowballLoc.getBlockY() < 0) {
								arena.waitingTimeSnowball(skylander);
								cancel();
								return;
							}
							
							if (snowball.isDead() || snowball.isOnGround()) {
								Float yaw = player.getLocation().getYaw();
								Float pitch = player.getLocation().getPitch();
								Location newLocation = snowball.getLocation().clone();
								
								newLocation.add(0, 1, 0);
								newLocation.setYaw(yaw);
								newLocation.setPitch(pitch);
								
								player.teleport(newLocation);
								
								arena.waitingTimeSnowball(skylander);
								
								cancel();
								return;
							}
						}
					}.runTaskTimer(plugin, 0, 3);
				}
			}
		} catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(ArenaListener, projectileLauch) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void blockFromTo(BlockFromToEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
    		if (event.getToBlock().getType() != Material.AIR || 
    			event.getBlock().getType().equals(Material.LAVA) || 
    			event.getBlock().getType().equals(Material.WATER)) 
    		{
    			event.setCancelled(true);
    		}
		}
		return;
	}
	
	@EventHandler
	public void hangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
		return;
	}
			
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockExplode(BlockExplodeEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockFade(BlockFadeEvent event) {
		event.setCancelled(true);
	}
}
