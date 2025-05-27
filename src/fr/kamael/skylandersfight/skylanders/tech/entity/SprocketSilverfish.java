package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class SprocketSilverfish extends CustomEntity {
	
	public SprocketSilverfish(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Silverfish silverfish = (Silverfish) player.getWorld().spawnEntity(location, EntityType.SILVERFISH);
		silverfish.setHealth(3);
		silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
		silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (silverfish == null || !silverfish.isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				silverfish.setTarget(SpellUtils.nearClosePlayer(plugin, skylander, silverfish, 30.).getPlayer());
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = silverfish;
	}
	
	public void onDamage(Skylander skylander) { 
		Silverfish silverfish = (Silverfish) this.entity;
		silverfish.removePotionEffect(PotionEffectType.INVISIBILITY);
		return; 
	}
	
	public Integer modifyDamage() { 
		return Sprocket.damageSecondMob; 
	}
}
