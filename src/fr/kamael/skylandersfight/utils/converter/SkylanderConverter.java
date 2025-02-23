package fr.kamael.skylandersfight.utils.converter;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;

public class SkylanderConverter {

	public static Integer convertForce(Double force) {
	    return (int) Math.round((force - 1.0) * 100);
	}

	public static Integer convertResis(Double defense) {
	    return (int) Math.round((defense - 1.0) * -100);
	}
	
	public static Skylander convert(String name, Player player) {
		
		switch (name) {
			case Spyro.name:
				return new Spyro(player);
				
			default:
				return null;
		}
	}
}
