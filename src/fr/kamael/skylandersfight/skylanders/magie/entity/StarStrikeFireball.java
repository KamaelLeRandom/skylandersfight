package fr.kamael.skylandersfight.skylanders.magie.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.magie.StarStrike;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;

public class StarStrikeFireball extends CustomEntity {
	
	public StarStrikeFireball(StarStrike starstrike, Location location) {
		super(starstrike, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL);
		fireball.setVelocity(new Vector(0, -0.5, 0)); 

		new BukkitRunnable() {
			private Integer timer = 200;
			private Double t = 0.;
			private Boolean hit = false;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				fireball.setVelocity(new Vector(0, -0.5, 0));
				
				// Condition d'arrêt.
				if (timer == 0 || hit || fireball.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					fireball.remove();
					cancel();
					return;
				}
				
		        // Particules.
		        t += Math.PI / 16;
		        double radius = 1.0;
		        for (int i = 0; i < 8; i++) {
		            double angle = i * (Math.PI / 4) + t;
		            double x = radius * Math.cos(angle);
		            double z = radius * Math.sin(angle);

		            double fireballX = fireball.getLocation().getX();
		            double fireballY = fireball.getLocation().getY();
		            double fireballZ = fireball.getLocation().getZ();

		            fireball.getWorld().spawnParticle(
		                Particle.FLAME,
		                fireballX + x, fireballY, fireballZ + z,
		                0, 0, 0, 0, 0);
		        }
		        
				// Dégats aux joueurs proches.
				for (Skylander skylander : SpellUtils.skylanderAround(plugin, skylander, entity.getLocation(), StarStrike.rangeFireballFirstSpell, 2.5, StarStrike.rangeFireballFirstSpell)) {
					Player playerHit = skylander.getPlayer();
					playerHit.getWorld().spawnParticle(
						Particle.EXPLOSION_LARGE, 
						entity.getLocation(), 
						2, 
						0.05, 0.05, 0.05,
						0
				    );
					playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence " + StarStrike.nameFirstSpell + "§f de §d" + skylander.getPlayer().getName() + "§f.");
					playerHit.sendTitle(StarStrike.nameFirstSpell, "§7Étourdissement de " + SkylanderConverter.convertTicks(StarStrike.tickStunFirstSpell) + "s.");
					playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					playerHit.damage(StarStrike.damageFireballFirstSpell, player);
					skylander.addStatus(StarStrike.tickStunFirstSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
					hit = true;
				}
		
				timer--;
			}
		}.runTaskTimer(plugin, 0, 1);
	
		this.entity = fireball;
	}
}
