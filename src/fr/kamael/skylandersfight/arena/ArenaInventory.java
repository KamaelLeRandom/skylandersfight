package fr.kamael.skylandersfight.arena;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.arena.map.CanyonPerdu;
import fr.kamael.skylandersfight.arena.map.CiteFlamme;
import fr.kamael.skylandersfight.arena.map.CrypteMaudite;
import fr.kamael.skylandersfight.arena.map.DesertRoyale;
import fr.kamael.skylandersfight.arena.map.JungleProfonde;
import fr.kamael.skylandersfight.arena.map.NouveauLabogda;
import fr.kamael.skylandersfight.arena.map.ParadisBlanc;
import fr.kamael.skylandersfight.arena.map.VestigesAzteques;
import fr.kamael.skylandersfight.arena.map.VillageTemTepe;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class ArenaInventory {
	public static Inventory getInventory() {
		Inventory inventory = Bukkit.createInventory(null, 36, Constants.inventoryArenaName);
		
		for (int i = 0; i < 9; i++) {
			inventory.setItem(i, ItemManager.getInventoryGlass());
			inventory.setItem(27 + i, ItemManager.getInventoryGlass());
		}
		inventory.setItem(9, ItemManager.getInventoryGlass());
		inventory.setItem(17, ItemManager.getInventoryGlass());
		inventory.setItem(18, ItemManager.getInventoryGlass());
		inventory.setItem(26, ItemManager.getInventoryGlass());

		inventory.setItem(10, NouveauLabogda.getItem());
		inventory.setItem(11, VillageTemTepe.getItem());
		inventory.setItem(12, ParadisBlanc.getItem());
		inventory.setItem(13, JungleProfonde.getItem());
		inventory.setItem(14, VestigesAzteques.getItem());
		inventory.setItem(15, CanyonPerdu.getItem());
		inventory.setItem(16, DesertRoyale.getItem());
		inventory.setItem(18, CiteFlamme.getItem());
		inventory.setItem(19, CrypteMaudite.getItem());
		
		return inventory;
	}
}
