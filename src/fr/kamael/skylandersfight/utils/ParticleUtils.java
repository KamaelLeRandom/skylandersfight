package fr.kamael.skylandersfight.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Plugin;

public class ParticleUtils {
	public static void tornadoParticule(Location loc, Particle p) {
		Float y = (float) loc.getY();
		Float radius = 0f;
		Float increase = 0.005f;
		
		for (Double t = 0.; t < 20; t += 0.05) {
			Float x = radius*(float)Math.sin(t);
			Float z = radius*(float)Math.cos(t);

			loc.getWorld().spawnParticle(p, loc.getX() + x, y, loc.getZ() + z, 1, 0, 0, 0, 0);
			
			y += 0.01f;
			radius += increase;
		}
	}
	
	public static void sphereParticule(Plugin plugin, Location loc, Particle p, Double rayon) {		
		new BukkitRunnable() {
			double phi = 0;
			double r = rayon;
			@Override
			public void run() {
				phi += Math.PI/10;
				for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI/40) {
					double x = r * Math.cos(theta)*Math.sin(phi);
					double y = r * Math.cos(phi) + 1.5;
					double z = r * Math.sin(theta)*Math.sin(phi);
					
					loc.add(x, y, z);
					loc.getWorld().spawnParticle(p, loc, 0, 0., 0., 0.);
					loc.subtract(x, y, z);
				}
				
				if (phi > Math.PI) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}
