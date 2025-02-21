package fr.kamael.skylandersfight.utils.runnable;

import fr.kamael.skylandersfight.skylanders.Skylander;

@FunctionalInterface
public interface SkylanderDamageRunnable {
    void execute(Skylander attacker, Skylander target);
}