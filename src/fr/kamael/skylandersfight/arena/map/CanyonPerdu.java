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

public class CanyonPerdu extends Arena {
	public static final String nameArena = "§6Canyon Perdu";

	public CanyonPerdu() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1431.5, 35.5, 838.5)); // Arche
		this.playerSpawns.add(new Location(w, -1388.5, 22.5, 787.5)); // Tente
		this.playerSpawns.add(new Location(w, -1472.5, 22.5, 796.5)); // Centre
		this.playerSpawns.add(new Location(w, -1501.5, 35.5, 832.5)); // Rail
		this.playerSpawns.add(new Location(w, -1465.5, 11.5, 777.5)); // Grotte Rail
		this.playerSpawns.add(new Location(w, -1462.5, 11.5, 824.5)); // Grotte Diamant
		this.playerSpawns.add(new Location(w, -1478.5, 28.5, 778.5)); // Grotte Entrée
		this.playerSpawns.add(new Location(w, -1497.5, 22.5, 819.5)); // Sortie
		
		this.healSpawns.add(new Location(w, -1387, 27, 815));
		this.healSpawns.add(new Location(w, -1489, 36, 838));
		this.healSpawns.add(new Location(w, -1432, 34, 776));
		this.healSpawns.add(new Location(w, -1458, 12, 824));
		this.healSpawns.add(new Location(w, -1477, 12, 795));
		this.healSpawns.add(new Location(w, -1470, 26, 808));
		
		this.itemSpawns.add(new Location(w, -1388.5, 22, 827.5));
		this.itemSpawns.add(new Location(w, -1434.5, 34, 777.5));
		this.itemSpawns.add(new Location(w, -1453.5, 19, 832.5));
		this.itemSpawns.add(new Location(w, -1424.5, 58, 701.5));
		this.itemSpawns.add(new Location(w, -1488.5, 11, 800.5));
		this.itemSpawns.add(new Location(w, -1487.5, 39, 839.5));
		this.itemSpawns.add(new Location(w, -1481.5, 22, 807.5));
		
		this.elements.add(Element.MAGIE);
		this.elements.add(Element.TERRE);
	}

	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(12500);
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§fBonus Élémentaire : §7"+ Element.MAGIE.getName() + " | " + Element.TERRE.getName())
		);
		ItemStack item = new ItemStack(Material.TERRACOTTA, 1);
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
