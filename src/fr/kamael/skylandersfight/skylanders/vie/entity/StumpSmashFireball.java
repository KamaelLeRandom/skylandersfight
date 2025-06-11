package fr.kamael.skylandersfight.skylanders.vie.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.vie.StumpSmash;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class StumpSmashFireball extends CustomEntity {
	
	public StumpSmashFireball(StumpSmash stumpsmash, Location location) {
		super(stumpsmash, location);
	}

	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		Fireball fireball = (Fireball) player.getWorld().spawnEntity(location, EntityType.FIREBALL);
		fireball.setVelocity(player.getLocation().clone().getDirection().multiply(1.5).normalize()); 
		fireball.setGravity(false);
		fireball.setIsIncendiary(false);
		fireball.setFireTicks(0);
		fireball.setYield(0);
		
		new BukkitRunnable() {
			private int timer = 60;
			
			@Override
			public void run() {				
				ArrayList<Skylander> listSkylanders = SpellUtils.skylanderAround(plugin, skylander, fireball.getLocation(), StumpSmash.rangeDetectPoisonSecondSpell, StumpSmash.rangeDetectPoisonSecondSpell, StumpSmash.rangeDetectPoisonSecondSpell);
				
				if (!listSkylanders.isEmpty()) {
					for (Skylander skylanderHit : listSkylanders) {
						damage(skylanderHit);
					}

					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
					fireball.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, fireball.getLocation(), 1, 0., 0., 0.);
					
					removeEntity();
					cancel();
					return;
				}
				
				if (timer == 0 || fireball.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					removeEntity();
					cancel();
					return;
				}
				
				fireball.getWorld().spawnParticle(Particle.LAVA, fireball.getLocation(), 1, 0., 0., 0.);
				fireball.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, fireball.getLocation(), 1, 0., 0., 0.);
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 5);
		
		this.entity = fireball;
	}
	
	@Override
	public void onHit(Skylander skylanderDamager) {				
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, entity.getLocation(), StumpSmash.rangeDetectPoisonSecondSpell, StumpSmash.rangeDetectPoisonSecondSpell, StumpSmash.rangeDetectPoisonSecondSpell)) {
			damage(skylanderHit);
		}
		
		entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, entity.getLocation(), 1, 0., 0., 0.);

		removeEntity();
		return;
	}
	
	public void damage(Skylander skylanderHit) {
		Player playerHit = skylanderHit.getPlayer();
		playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		playerHit.sendMessage(Constants.prefixMessage+ "Vous venez d'être touché par l'explosion de la compétence "+ StumpSmash.nameSecondSpell + "§f de §4"+ skylander.getPlayer().getName() +"§f.");
		playerHit.addPotionEffect(new PotionEffect(PotionEffectType.POISON, StumpSmash.secDurationPoisonSecondSpell * 20, 1, false, false));
	}
}
