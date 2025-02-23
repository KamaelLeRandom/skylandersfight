package fr.kamael.skylandersfight.utils.converter;

import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;

public class ArenaConverter {
	
	public static Arena convert(String name) {
		switch (name) {
			case "§bParadis Blanc": {
				return new ParadisBlanc();
			}
			default:
				return null;
		}
	}
}
