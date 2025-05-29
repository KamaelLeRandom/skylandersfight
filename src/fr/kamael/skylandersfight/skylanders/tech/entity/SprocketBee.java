package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.entity.AggressiveBee;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import net.minecraft.server.level.WorldServer;

public class SprocketBee extends CustomEntity {
	
	public SprocketBee(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		AggressiveBee bee = new AggressiveBee(player.getLocation(), Sprocket.nameFirstMob, 10, skylander);
		WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
		world.addEntity(bee);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (bee == null || !bee.getBukkitEntity().isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = bee.getBukkitEntity();
	}
	
	public Integer modifyDamage() { 
		return Sprocket.damageFirstMob; 
	}
	
	public void onDamage(Skylander skylander) { 
		skylander.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false));
		return; 
	}
}
