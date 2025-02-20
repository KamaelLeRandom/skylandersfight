package fr.kamael.skylandersfight.utils.manager;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkManager {
	public static void launchVictoryFirework(Location location) {
	    Firework firework = location.getWorld().spawn(location, Firework.class);
	    FireworkMeta meta = firework.getFireworkMeta();
	    meta.addEffect(FireworkEffect.builder()
	            .withColor(Color.ORANGE, Color.YELLOW) 
	            .withFade(org.bukkit.Color.WHITE)
	            .with(FireworkEffect.Type.STAR)
	            .flicker(true)
	            .trail(true)
	            .build());
	    meta.addEffect(FireworkEffect.builder()
	            .withColor(Color.RED, org.bukkit.Color.ORANGE)
	            .withFade(Color.YELLOW) 
	            .with(FireworkEffect.Type.BALL_LARGE)
	            .trail(true)
	            .build());
	    meta.setPower(1);
	    firework.setFireworkMeta(meta);
	}
}
