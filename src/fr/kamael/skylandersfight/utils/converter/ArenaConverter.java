package fr.kamael.skylandersfight.utils.converter;

import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.map.JungleProfonde;
import fr.kamael.skylandersfight.arena.map.NouveauLabogda;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;
import fr.kamael.skylandersfight.arena.map.VestigesAzteques;
import fr.kamael.skylandersfight.arena.map.VillageTemTepe;

public class ArenaConverter {
	
	public static Arena convert(String name) {
		switch (name) {
			case ParadisBlanc.nameArena:
				return new ParadisBlanc();
			case JungleProfonde.nameArena:
				return new JungleProfonde();
			case NouveauLabogda.nameArena:
				return new NouveauLabogda();
			case VillageTemTepe.nameArena:
				return new VillageTemTepe();
			case VestigesAzteques.nameArena:
				return new VestigesAzteques();
				
			default:
				return null;
		}
	}
}
