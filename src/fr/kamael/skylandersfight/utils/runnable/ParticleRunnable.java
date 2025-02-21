package fr.kamael.skylandersfight.utils.runnable;

import org.bukkit.Location;

@FunctionalInterface
public interface ParticleRunnable {
    void execute(Location location);
}