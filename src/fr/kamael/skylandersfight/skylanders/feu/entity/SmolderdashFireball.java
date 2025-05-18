package fr.kamael.skylandersfight.skylanders.feu.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.feu.Smolderdash;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class SmolderdashFireball extends CustomEntity {
	private Double damageExplosion = 0.;
	
	public SmolderdashFireball(Smolderdash smolderdash, Location location) {
		super(smolderdash, location);
	}

	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL);
		fireball.setDirection(new Vector(0, 0, 0));
		fireball.setVelocity(new Vector(0, 0, 0)); 
		fireball.setGravity(false);
		fireball.setIsIncendiary(false);
		fireball.setFireTicks(0);
		
		new BukkitRunnable() {
			private Integer timer = Smolderdash.tickFireballFirstSpell;
			private Double t = 0.;
			
			@Override
			public void run() {
				damageExplosion += Smolderdash.bonusDamageFireballFirstSpell;
				fireball.setVelocity(new Vector(0, 0, 0));
				
				if (timer == 0 || fireball.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					fireball.remove();
					cancel();
					return;
				}
				
		        t += Math.PI / 16;
		        double radius = 1.0;
	            double fireballX = fireball.getLocation().getX();
	            double fireballY = fireball.getLocation().getY();
	            double fireballZ = fireball.getLocation().getZ();
		        
		        for (int i = 0; i < 8; i++) {
		            double angle = i * (Math.PI / 4) + t;
		            double x = radius * Math.cos(angle);
		            double z = radius * Math.sin(angle);

		            fireball.getWorld().spawnParticle(
		                Particle.FLAME,
		                fireballX + x, fireballY, fireballZ + z,
		                0, 0, 0, 0, 0);
		        }
			}
		}.runTaskTimer(plugin, 0, 1);
		
		this.entity = fireball;
	}
	
	@Override
	public void onHit(Skylander skylanderDamager) {
		if (skylander.equals(skylanderDamager)) {
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, entity.getLocation(), 6., 4., 6.)) {
				skylanderHit.getPlayer().damage(damageExplosion, skylander.getPlayer());
			}
			
			entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, entity.getLocation(), 1, 0., 0., 0.);
			entity.remove();
		} else {
			entity.getWorld().spawnParticle(Particle.SMOKE_LARGE, entity.getLocation(), 0, 0, 0, 0, 0);
			entity.remove();
		}
	}
}
