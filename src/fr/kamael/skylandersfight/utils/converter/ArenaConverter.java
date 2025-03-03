package fr.kamael.skylandersfight.utils.converter;

import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.map.JungleProfonde;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;

public class ArenaConverter {
	
	public static Arena convert(String name) {
		switch (name) {
			case ParadisBlanc.nameArena:
				return new ParadisBlanc();
			case JungleProfonde.nameArena:
				return new JungleProfonde();
				
			default:
				return null;
		}
	}
}
