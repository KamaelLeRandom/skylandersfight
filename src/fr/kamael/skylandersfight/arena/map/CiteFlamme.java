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

public class CiteFlamme extends Arena {
	public static final String nameArena = "§cCité de la Flamme";

	public CiteFlamme() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -850.5, 32.5, -1098.5)); // Église
		this.playerSpawns.add(new Location(w, -860.5, 22.5, -1156.5)); // Cabanon
		this.playerSpawns.add(new Location(w, -890.5, 22.5, -1130.5)); // Arbre
		this.playerSpawns.add(new Location(w, -903.5, 20.5, -1150.5)); // Entrepot
		this.playerSpawns.add(new Location(w, -904.5, 24.5, -1097.5)); // Mini-Arbre
		this.playerSpawns.add(new Location(w, -870.5,  6.5, -1137.5)); // Grotte Principal
		this.playerSpawns.add(new Location(w, -860.5,  3.5, -1094.5)); // Flamme Géante
		this.playerSpawns.add(new Location(w, -886.5,  9.5, -1095.5)); // Grotte Nénuphar
		
		this.healSpawns.add(new Location(w, -909, 23, -1109));
		this.healSpawns.add(new Location(w, -908, 22, -1140));
		this.healSpawns.add(new Location(w, -849, 21, -1160));
		this.healSpawns.add(new Location(w, -851, 30, -1107));
		this.healSpawns.add(new Location(w, -849, 21, -1112));
		this.healSpawns.add(new Location(w, -851,  9, -1099));
		this.healSpawns.add(new Location(w, -889,  4, -1122));
		this.healSpawns.add(new Location(w, -880, 13, -1097));
		
		this.itemSpawns.add(new Location(w, -904.5,  5, -1126.5));
		this.itemSpawns.add(new Location(w, -885.5,  8, -1151.5));
		this.itemSpawns.add(new Location(w, -851.5, 22, -1152.5));
		this.itemSpawns.add(new Location(w, -852.5, 31, -1110.5));
		this.itemSpawns.add(new Location(w, -885.5, 21, -1096.5));
		this.itemSpawns.add(new Location(w, -899.5, 20, -1156.5));
		this.itemSpawns.add(new Location(w, -863.5, 12, -1092.5));
		
		this.elements.add(Element.MAGIE);
		this.elements.add(Element.FEU);
	}
	
	@Override
	public void deathmatch() {
		deathmatch = true;
		this.playerSpawns.removeAll(this.playerSpawns);
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1500.5, 3.5, 105.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1490.5, 3.5, 115.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1501.5, 3.5, 117.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1483.5, 3.5, 100.5));
		return;
	}
	
	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(23000);
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§fBonus Élémentaire : §7"+ Element.MAGIE.getName() + " | " + Element.FEU.getName())
		);
		ItemStack item = new ItemStack(Material.BIRCH_LEAVES, 1);
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
