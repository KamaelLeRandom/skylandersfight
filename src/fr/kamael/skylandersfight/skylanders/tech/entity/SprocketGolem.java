package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.entity.AggressiveGolem;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.utils.SpellUtils;
import net.minecraft.server.level.WorldServer;

public class SprocketGolem extends CustomEntity {

	public SprocketGolem(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		AggressiveGolem golem = new AggressiveGolem(player.getLocation(), Sprocket.nameThirdMob, 20, skylander);
		WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
		world.addEntity(golem);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (golem == null || !golem.getBukkitEntity().isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = golem.getBukkitEntity();
	}
	
	@Override
	public void onDeath() { 
		entity.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, this.entity.getLocation(), 1);
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this.skylander, this.entity.getLocation(), Sprocket.rangeExplosionThirdMob, Sprocket.rangeExplosionThirdMob, Sprocket.rangeExplosionThirdMob)) {
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
