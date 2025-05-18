package fr.kamael.skylandersfight.skylanders.feu.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class EruptorFireball extends CustomEntity {
	public static final Double rangeFireballExplosion = 3.; 

	public EruptorFireball(Eruptor eruptor, Location location) {
		super(eruptor, location);
	}
	
	@Override
	public void summon() {
		Player player = this.skylander.getPlayer();
		
		Fireball fireball = (Fireball) player.getWorld().spawnEntity(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2)), EntityType.FIREBALL);
		fireball.setYield(0);
		
		new BukkitRunnable() {
			private int timer = 60;
			
			@Override
			public void run() {				
				ArrayList<Skylander> listSkylanders = SpellUtils.skylanderAround(plugin, skylander, fireball.getLocation(), rangeFireballExplosion, 2.5, rangeFireballExplosion);
				
				if (!listSkylanders.isEmpty()) {
					for (Skylander skylanderHit : listSkylanders) {
						Player playerHit = skylanderHit.getPlayer();
						
						playerHit.playSound(fireball.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
						playerHit.sendMessage(Constants.prefixMessage+ "Vous venez d'être touché par l'explosion de la compétence "+ Eruptor.nameFirstSpell + "§f de §4"+ player.getName() +"§f.");
						playerHit.damage(Eruptor.damageFirstSpell, player);
						playerHit.setFireTicks(8 * 20);
						fireball.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, fireball.getLocation(), 1, 0., 0., 0.);

					}
					
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
		Player player = this.skylander.getPlayer();
		
		this.entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, this.entity.getLocation(), 1, 0., 0., 0.);
		
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, this.entity.getLocation(), rangeFireballExplosion, 2.5, rangeFireballExplosion)) {
			Player playerHit = skylanderHit.getPlayer();
			
			playerHit.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			playerHit.sendMessage(Constants.prefixMessage+ "Vous venez d'être touché par l'explosion de la compétence "+ Eruptor.nameFirstSpell + "§f de §4"+ player.getName() +"§f.");
			playerHit.damage(Eruptor.damageFirstSpell, player);
			playerHit.setFireTicks(8 * 20);
		}
		
		this.entity.remove();
		return;
	}
}
