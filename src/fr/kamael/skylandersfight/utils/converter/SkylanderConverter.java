package fr.kamael.skylandersfight.utils.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.air.LightningRod;
import fr.kamael.skylandersfight.skylanders.eau.SlamBam;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.skylanders.mort.GhostRoaster;
import fr.kamael.skylandersfight.skylanders.tech.TriggerHappy;
import fr.kamael.skylandersfight.skylanders.terre.Terrafin;
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;

public class SkylanderConverter {

	public static Integer convertForce(Double force) {
	    return (int) Math.round((force - 1.0) * 100);
	}

	public static Integer convertResis(Double defense) {
	    return (int) Math.round((defense - 1.0) * -100);
	}
	
    public static String convertTicks(Integer ticks) {
        BigDecimal seconds = new BigDecimal(ticks).divide(new BigDecimal(20), 10, RoundingMode.HALF_UP);
        return seconds.stripTrailingZeros().toPlainString();
    }
	
	public static Skylander convert(String name, Player player) {
		
		switch (name) {
			case Spyro.name:
				return new Spyro(player);
			case TriggerHappy.name:
				return new TriggerHappy(player);
			case StealthElf.name:
				return new StealthElf(player);
			case GhostRoaster.name:
				return new GhostRoaster(player);
			case Eruptor.name:
				return new Eruptor(player);
			case SlamBam.name:
				return new SlamBam(player);
			case Terrafin.name: 
				return new Terrafin(player);
			case LightningRod.name: 
				return new LightningRod(player);
				
			default:
				return null;
		}
	}
}
