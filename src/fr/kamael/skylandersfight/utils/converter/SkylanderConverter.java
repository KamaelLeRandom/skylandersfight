package fr.kamael.skylandersfight.utils.converter;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;

public class SkylanderConverter {

	public static Skylander convert(String name, Player player) {
		
		switch (name) {
			case Spyro.name:
				return new Spyro(player);
				
			default:
				return null;
		}
	}
}
