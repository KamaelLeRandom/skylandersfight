package fr.kamael.skylandersfight.arena;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;

public class ArenaListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
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
	public void entityPickupItem(EntityPickupItemEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
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
	public void blockPlace(BlockPlaceEvent event) {
		if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockFade(BlockFadeEvent event) {
		event.setCancelled(true);
	}
}
