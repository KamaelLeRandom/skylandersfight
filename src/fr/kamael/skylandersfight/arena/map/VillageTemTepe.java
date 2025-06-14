package fr.kamael.skylandersfight.arena.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.skylanders.Element;

public class VillageTemTepe extends Arena {
	public static final String nameArena = "§dVillage Tem'Tepe";
	public static final String nameEvent = "§d-";
	
	public VillageTemTepe() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1300.5, 18.5, -1039.5)); // Lac
		this.playerSpawns.add(new Location(w, -1355.5, 22.5, -1057.5)); // Fermier
		this.playerSpawns.add(new Location(w, -1362.5, 17.5, -1021.5)); // Maison Cyril
		this.playerSpawns.add(new Location(w, -1367.5, 15.5,  -999.5)); // Grotte
		this.playerSpawns.add(new Location(w, -1319.5, 21.5,  -992.5)); // Sud
		this.playerSpawns.add(new Location(w, -1288.5, 18.5, -1004.5)); // Bucheron
		this.playerSpawns.add(new Location(w, -1283.5, 17.5, -1049.5)); // Maison Pecheur
		this.playerSpawns.add(new Location(w, -1313.5, 17.5, -1062.5)); // Nord
		
		this.healSpawns.add(new Location(w, -1285, 18, -1042)); // Crapaud
		this.healSpawns.add(new Location(w, -1360, 20, -1044)); // Epouvential
		this.healSpawns.add(new Location(w, -1312, 21, -1020)); // Marché
		this.healSpawns.add(new Location(w, -1354, 16,  -998)); // Grotte
		this.healSpawns.add(new Location(w, -1360, 18, -1032)); // Chariot
		this.healSpawns.add(new Location(w, -1322, 16, -1035)); // Feu de Camp
		this.healSpawns.add(new Location(w, -1284, 19, -1009)); // Foret
		this.healSpawns.add(new Location(w, -1367, 22, -1016)); // Maison Cyril
		
		this.itemSpawns.add(new Location(w, -1311.5, 17.5, -1019.5)); // Magasin Vert
		this.itemSpawns.add(new Location(w, -1330.5, 17.5, -1019.5)); // Magasin Cyan
		this.itemSpawns.add(new Location(w, -1335.5, 17.5, -1040.5)); // Magasin Rouge
		this.itemSpawns.add(new Location(w, -1350.5, 17.5, -1033.5)); // Magasin Rose
		
		this.elements.add(Element.VIE);
		this.elements.add(Element.BOGDA);
	}
	
	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(0);
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§fBonus Élémentaire : §7"+ Element.VIE.getName() + " | " + Element.BOGDA.getName())
		);
		ItemStack item = new ItemStack(Material.OAK_LOG, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameArena);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
