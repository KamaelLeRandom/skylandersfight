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

public class DesertRoyale extends Arena {
	public static final String nameArena = "§eDésert Royale";
	public static final String nameEvent = "§e-";
	
	public DesertRoyale() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -563.0, 14.5, -487.0)); // Cactus
		this.playerSpawns.add(new Location(w, -644.5, 18.5, -496.5)); // Oasis
		this.playerSpawns.add(new Location(w, -611.0, 14.5, -476.0)); // Haut Pyramide
		this.playerSpawns.add(new Location(w, -637.5,  4.5, -448.5)); // Village
		this.playerSpawns.add(new Location(w, -596.5, 10.5, -440.5)); // Dune
		this.playerSpawns.add(new Location(w, -567.5,  5.5, -446.5)); // Lac
		this.playerSpawns.add(new Location(w, -615.5,  4.5, -480.5)); // Intérieur Pyramide
		this.playerSpawns.add(new Location(w, -597.5,  4.5, -494.5)); // Puit
		
		this.healSpawns.add(new Location(w, -615, 4, -450));
		this.healSpawns.add(new Location(w, -622, 4, -442));
		this.healSpawns.add(new Location(w, -574, 4, -442));
		this.healSpawns.add(new Location(w, -564, 9, -491));
		this.healSpawns.add(new Location(w, -575, 4, -497));
		this.healSpawns.add(new Location(w, -610, 4, -495));
		this.healSpawns.add(new Location(w, -645, 17, -499));
		this.healSpawns.add(new Location(w, -636, 4, -475));
		this.healSpawns.add(new Location(w, -619, 4, -472));
		this.healSpawns.add(new Location(w, -604, 4, -482));
		this.healSpawns.add(new Location(w, -614, 9, -473));
		this.healSpawns.add(new Location(w, -572, 2, -471));
		this.healSpawns.add(new Location(w, -647, 6, -509));
		
		this.itemSpawns.add(new Location(w, -650.5,  4.5, -489.5)); // Oasis
		this.itemSpawns.add(new Location(w, -612.5,  4.5, -441.5)); // Maison
		this.itemSpawns.add(new Location(w, -611.0,  4.5, -476.0)); // Pyramide
		this.itemSpawns.add(new Location(w, -583.5,  4.5, -440.5)); // Lac
		this.itemSpawns.add(new Location(w, -574.5,  4.5, -490.5)); // Cactus
		this.itemSpawns.add(new Location(w, -611.0,  4.5, -488.0)); // Derrière Pyramide
		
		this.elements.add(Element.MORT);
		this.elements.add(Element.TERRE);
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
		Bukkit.getWorld("world").setTime(1000);
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§fBonus Élémentaire : §7"+ Element.MORT.getName() + " | " + Element.TERRE.getName())
		);
		ItemStack item = new ItemStack(Material.SANDSTONE, 1);
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
