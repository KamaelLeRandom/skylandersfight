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
import fr.kamael.skylandersfight.utils.TraversableBlocksUtils;

public class ZooLouChicken extends CustomEntity {

	public ZooLouChicken(ZooLou zoolou, Location location) {
		super(zoolou, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
	    Location initialLocation = player.getLocation().add(0, 1, 0);
		Chicken chicken = (Chicken) player.getWorld().spawnEntity(initialLocation, EntityType.CHICKEN);
		chicken.setGravity(false);
		chicken.setInvulnerable(true);
		Vector vector = player.getLocation().getDirection().multiply(0.5);
		
		new BukkitRunnable() {
			private Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.0F);
			private Integer timer = 200;
			private Boolean hit = false;
			private Location lastLocation = null;
			
			@Override
			public void run() {
				if (lastLocation == null) {
					lastLocation = chicken.getLocation();
				} else {
					hit = lastLocation.distance(chicken.getLocation()) < 0.5;
					lastLocation = chicken.getLocation();
				}
				
				if (hit || timer == 0 || chicken == null || chicken.isDead() || !TraversableBlocksUtils.isTraversableBlock(chicken.getLocation().clone().subtract(0, 0.1, 0).getBlock().getType()) || !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					explosionParticule();
					removeEntity();
					cancel();
					return;
				}
				
				chicken.setVelocity(vector);
				chicken.getWorld().spawnParticle(Particle.REDSTONE, chicken.getLocation().add(0, 0.2, 0), 5, 0.1, 0.1, 0.1, 0, dust);

				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, chicken.getLocation(), 1., 1., 1.)) {
					Player playerHit = skylanderHit.getPlayer();
					playerHit.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					playerHit.sendMessage(Constants.prefixMessage + "Vous avez été touché par la compétence " + ZooLou.namePassif + "§f de §a" + player.getPlayer() + "§f.");
					playerHit.damage(ZooLou.damagePassif, player);
					hit = true;
				}
				
				if (hit) {
					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
					explosionParticule();
					removeEntity();
					cancel();
					return;
				}
				
				timer--;
			}
			
			private void explosionParticule() {			    
			    Location loc = chicken.getLocation();
			    for (double phi = 0; phi < Math.PI; phi += Math.PI / 10) {
			        for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 10) {
			            double r = 1.75;
			            double x = r * Math.sin(phi) * Math.cos(theta);
			            double y = r * Math.cos(phi);
			            double z = r * Math.sin(phi) * Math.sin(theta);
			            Location particleLoc = loc.clone().add(x, y, z);
				        particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dust);
			        }
			    }
			}
		}.runTaskTimer(plugin, 0, 2);
		
		this.entity = chicken;
	}
}
