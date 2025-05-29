package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.entity.AggressiveSilverfish;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import net.minecraft.server.level.WorldServer;

public class SprocketSilverfish extends CustomEntity {
	
	public SprocketSilverfish(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
	    WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
	    
		AggressiveSilverfish silverfish = new AggressiveSilverfish(player.getLocation(), Sprocket.nameSecondMob, 15, skylander);
		world.addEntity(silverfish);

		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (silverfish == null || !silverfish.getBukkitEntity().isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = silverfish.getBukkitEntity();
	}
	
	public Integer modifyDamage() { 
		return Sprocket.damageSecondMob; 
	}
}
