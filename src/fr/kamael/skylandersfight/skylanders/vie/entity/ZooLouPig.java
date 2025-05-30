package fr.kamael.skylandersfight.skylanders.vie.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;

public class ZooLouPig extends CustomEntity {

	public ZooLouPig(ZooLou zoolou, Location location) {
		super(zoolou, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Pig pig = (Pig) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
        pig.setAI(false);
        pig.setCollidable(false);
        pig.setInvulnerable(true);
        pig.setSaddle(true);
        pig.addPassenger(player);
        
        new BukkitRunnable() {
        	private Integer timer = 100;
			
			@Override
			public void run() {
				if (timer == 0 || pig == null || pig.isDead() || !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					removeEntity();
					cancel();
					return;
				}
				
				if (timer % 20 == 0) {
					pig.setVelocity(new Vector(0, 0.8, 0));
				} else {
					pig.setVelocity(player.getLocation().getDirection().multiply(0.5));
				}
					
				timer--;
			}
		}.runTaskTimer(plugin, 0, 2);
	}
}
