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

public class Purgatoire extends Arena {
	public static final String nameArena = "§4Le Purgatoire";

	public Purgatoire() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1845, 23, 345));
		this.playerSpawns.add(new Location(w, -1845, 23, 284));
		this.playerSpawns.add(new Location(w, -1883, 22, 341));
		this.playerSpawns.add(new Location(w, -1839, 21, 383));
		this.playerSpawns.add(new Location(w, -1804, 26, 343));
		this.playerSpawns.add(new Location(w, -1846, 14, 331));
		
		this.healSpawns.add(new Location(w, -1837, 27, 319));
		this.healSpawns.add(new Location(w, -1850, 25, 287));
		this.healSpawns.add(new Location(w, -1883, 16, 350));
		this.healSpawns.add(new Location(w, -1849, 18, 347));
		this.healSpawns.add(new Location(w, -1795, 25, 336));
		this.healSpawns.add(new Location(w, -1817, 22, 306));
		this.healSpawns.add(new Location(w, -1845, 22, 352));
		
		this.itemSpawns.add(new Location(w, -1845, 23, 345));
		this.itemSpawns.add(new Location(w, -1845, 23, 284));
		this.itemSpawns.add(new Location(w, -1883, 22, 341));
		this.itemSpawns.add(new Location(w, -1839, 21, 383));
		this.itemSpawns.add(new Location(w, -1804, 26, 343));
		this.itemSpawns.add(new Location(w, -1846, 14, 331));
		
		this.elements.add(Element.FEU);
		this.elements.add(Element.AIR);
	}
	
	@Override
	public void onStart() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(14000);
	}
	
	@Override
	public void event() {
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§7Bonus Élémentaire : " + Element.FEU.getName() + " §7| " + Element.AIR.getName()
		));

		ItemStack item = new ItemStack(Material.NETHERRACK, 1);
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
