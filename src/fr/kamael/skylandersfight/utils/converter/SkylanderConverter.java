package fr.kamael.skylandersfight.utils.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.air.JetVac;
import fr.kamael.skylandersfight.skylanders.air.LightningRod;
import fr.kamael.skylandersfight.skylanders.air.Scratch;
import fr.kamael.skylandersfight.skylanders.air.Warnado;
import fr.kamael.skylandersfight.skylanders.eau.Chill;
import fr.kamael.skylandersfight.skylanders.eau.SlamBam;
import fr.kamael.skylandersfight.skylanders.eau.WhamShell;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.skylanders.feu.Smolderdash;
import fr.kamael.skylandersfight.skylanders.feu.Sunburn;
import fr.kamael.skylandersfight.skylanders.magie.DoubleTrouble;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.skylanders.magie.StarStrike;
import fr.kamael.skylandersfight.skylanders.magie.Voodood;
import fr.kamael.skylandersfight.skylanders.mort.GhostRoaster;
import fr.kamael.skylandersfight.skylanders.mort.GrimCreeper;
import fr.kamael.skylandersfight.skylanders.tech.Boomer;
import fr.kamael.skylandersfight.skylanders.tech.Drobot;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.skylanders.tech.TriggerHappy;
import fr.kamael.skylandersfight.skylanders.terre.PrismBreak;
import fr.kamael.skylandersfight.skylanders.terre.Terrafin;
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;

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
			case DoubleTrouble.name:
				return new DoubleTrouble(player);
			case Voodood.name:
				return new Voodood(player);
			case StarStrike.name:
				return new StarStrike(player);
			case TriggerHappy.name:
				return new TriggerHappy(player);
			case Drobot.name:
				return new Drobot(player);
			case Boomer.name:
				return new Boomer(player);
			case Sprocket.name:
				return new Sprocket(player);
			case StealthElf.name:
				return new StealthElf(player);
			case ZooLou.name:
				return new ZooLou(player);
			case GhostRoaster.name:
				return new GhostRoaster(player);
			case GrimCreeper.name:
				return new GrimCreeper(player);
			case Eruptor.name:
				return new Eruptor(player);
			case Sunburn.name:
				return new Sunburn(player);
			case Smolderdash.name: 
				return new Smolderdash(player);
			case SlamBam.name:
				return new SlamBam(player);
			case WhamShell.name:
				return new WhamShell(player);
			case Chill.name:
				return new Chill(player);
			case Terrafin.name: 
				return new Terrafin(player);
			case PrismBreak.name: 
				return new PrismBreak(player);
			case LightningRod.name: 
				return new LightningRod(player);
			case Warnado.name: 
				return new Warnado(player);
			case JetVac.name:
				return new JetVac(player);
			case Scratch.name:
				return new Scratch(player);
				
			default:
				return null;
		}
	}
}
