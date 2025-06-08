package fr.kamael.skylandersfight.arena;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.arena.map.JungleProfonde;
import fr.kamael.skylandersfight.arena.map.NouveauLabogda;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;

public class ArenaInventory {
	public static Inventory getInventory() {
		Inventory inventory = Bukkit.createInventory(null, 36, Constants.inventoryArenaName);
		inventory.setItem(11, ParadisBlanc.getItem());
		inventory.setItem(12, JungleProfonde.getItem());
		inventory.setItem(13, NouveauLabogda.getItem());
		return inventory;
	}
}
