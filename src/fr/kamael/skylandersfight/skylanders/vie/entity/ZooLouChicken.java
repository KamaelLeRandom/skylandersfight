package fr.kamael.skylandersfight.skylanders.vie.entity;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class ZooLouChicken extends CustomEntity {

	public ZooLouChicken(ZooLou zoolou, Location location) {
		super(zoolou, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
	    Location initialLocation = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(1));
		Chicken chicken = (Chicken) player.getWorld().spawnEntity(initialLocation, EntityType.CHICKEN);
		chicken.setAI(false);
		chicken.setGravity(false);
		chicken.setInvulnerable(true);
		chicken.teleport(player.getLocation());
		Vector vector = player.getLocation().getDirection().multiply(0.5);
		
		new BukkitRunnable() {
			private Boolean hit = false;
			
			@Override
			public void run() {
				if (chicken == null || chicken.isDead() || chicken.getLocation().add(vector).getBlock().getType().isSolid() || !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					removeEntity();
					cancel();
					return;
				}
				
				chicken.setVelocity(vector);
				
				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, chicken.getLocation(), 1., 1., 1.)) {
					Player playerHit = skylanderHit.getPlayer();
					playerHit.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					playerHit.sendMessage(Constants.prefixMessage + "");
					playerHit.damage(ZooLou.damagePassif);
					hit = true;
				}
				
				if (hit) {
					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
					
				    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.5F);
				    
				    Location loc = chicken.getLocation();
				    for (double phi = 0; phi < Math.PI; phi += Math.PI / 10) {
				        for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 10) {
				            double r = 1.5;
				            double x = r * Math.sin(phi) * Math.cos(theta);
				            double y = r * Math.cos(phi);
				            double z = r * Math.sin(phi) * Math.sin(theta);
				            Location particleLoc = loc.clone().add(x, y, z);
					        particleLoc.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, dustOptions);
				        }
				    }
					
					removeEntity();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
		
		this.entity = chicken;
	}
}
