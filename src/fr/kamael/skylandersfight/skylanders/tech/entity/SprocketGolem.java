package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class SprocketGolem extends CustomEntity {

	public SprocketGolem(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		IronGolem golem = (IronGolem) player.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
		golem.setHealth(20);
		golem.setPlayerCreated(false);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (golem == null || !golem.isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				golem.setTarget(SpellUtils.nearClosePlayer(plugin, skylander, golem, 30.).getPlayer());
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = golem;
	}
	
	@Override
	public void onDeath() { 
		this.entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, this.entity.getLocation(), 1);
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this.skylander, this.entity.getLocation(), 5., 5., 5.)) {
			Player playerHit = skylanderHit.getPlayer();
			playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par l'explosion de la construction " + Sprocket.nameThirdMob + "§f de §e" + this.skylander.getPlayer().getName() + "§f.");
			playerHit.damage(Sprocket.damageExplosionThirdMob, this.skylander.getPlayer());
		}
		return; 
	}	
	
	public Integer modifyDamage() { 
		return Sprocket.damageThirdMob; 
	}
}
