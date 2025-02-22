package fr.kamael.skylandersfight.game;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.ArenaInventory;
import fr.kamael.skylandersfight.skylanders.SkylanderInventory;

public class GameRound {
	private Plugin plugin = Plugin.plugin;
	private Arena arena;
	private Integer timerRound;
	private Integer numeroRound;
	
	public GameRound(Integer numeroRound) {
		this.numeroRound = numeroRound;
		this.timerRound = 0;
		
		chooseSkylander();
	}
	
	public void chooseSkylander() { 
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
			gamePlayer.getPlayer().openInventory(SkylanderInventory.getRandomInventory());
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Boolean isValid = true;
				
				for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
					if (gamePlayer.getSkylander() == null) {
						isValid = false;
					}
				}
				
				if (isValid) {
					chooseArena();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void chooseArena() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
			gamePlayer.getPlayer().openInventory(ArenaInventory.getInventory());
		}
		
		return; 
	}
}
