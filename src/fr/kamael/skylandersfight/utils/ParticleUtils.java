package fr.kamael.skylandersfight.utils;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleUtils {
	public static void tornadoParticule(Location loc, Particle p) {
		Float y = (float) loc.getY();
		Float radius = 0f;
		Float increase = 0.005f;
		
		for (Double t = 0.; t < 20; t += 0.05) {
			Float x = radius*(float)Math.sin(t);
			Float z = radius*(float)Math.cos(t);

			loc.getWorld().spawnParticle(p, loc.getX() + x, y, loc.getZ() + z, 1, 0., 0., 0.);
			
			y += 0.01f;
			radius += increase;
		}
	}
}
