package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class SprocketBee extends CustomEntity {
	
	public SprocketBee(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Bee bee = (Bee) player.getWorld().spawnEntity(location, EntityType.BEE);
		bee.setHealth(3);
		bee.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (bee == null || !bee.isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				bee.setTarget(SpellUtils.nearClosePlayer(plugin, bee, 30.).getPlayer());
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = bee;
	}
	
	public Integer modifyDamage() { 
		return Sprocket.damageFirstMob; 
	}
}
