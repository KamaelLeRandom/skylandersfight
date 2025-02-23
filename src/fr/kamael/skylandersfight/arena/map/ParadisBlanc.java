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
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class ParadisBlanc extends Arena {
	public static String nameArena = "§bParadis Blanc";
	public static String nameSnowball = "§bMarquage";
	
	public ParadisBlanc() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1525, 12, -151));
		this.playerSpawns.add(new Location(w, -1495, 17, -133));
		this.playerSpawns.add(new Location(w, -1510, 12, -106));
		this.playerSpawns.add(new Location(w, -1525, 7, -93));
		this.playerSpawns.add(new Location(w, -1462, 12, -96));
		this.playerSpawns.add(new Location(w, -1454, 6, -115));
		this.playerSpawns.add(new Location(w, -1456, 9, -144));
		this.playerSpawns.add(new Location(w, -1497, 41, -104));
		
		this.healSpawns.add(new Location(w, -1478, 32, -144));
		this.healSpawns.add(new Location(w, -1500, 28, -161));
		this.healSpawns.add(new Location(w, -1497, 40, -109));
		this.healSpawns.add(new Location(w, -1511, 7, -101));
		this.healSpawns.add(new Location(w, -1476, 6, -151));
		this.healSpawns.add(new Location(w, -1454, 4, -114));
		this.healSpawns.add(new Location(w, -1496, 20, -133));
		this.healSpawns.add(new Location(w, -1496, 20, -134));
		this.healSpawns.add(new Location(w, -1480, 6, -120));
		this.healSpawns.add(new Location(w, -1527, 10, -153));
		
		this.itemSpawns.add(new Location(w, -1525, 12, -151));
		this.itemSpawns.add(new Location(w, -1495, 17, -133));
		this.itemSpawns.add(new Location(w, -1510, 12, -106));
		this.itemSpawns.add(new Location(w, -1525, 7, -93));
		this.itemSpawns.add(new Location(w, -1462, 12, -96));
		this.itemSpawns.add(new Location(w, -1454, 6, -115));
		this.itemSpawns.add(new Location(w, -1456, 9, -144));
		this.itemSpawns.add(new Location(w, -1497, 41, -104));
		
		this.elements.add(Element.EAU);
		this.elements.add(Element.AIR);
	}

	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(1000);
		
		if (plugin.game.getConfig().getActiveEventMap()) {
			Bukkit.broadcastMessage("§bMarquage§f est activé, toutes les 30s vous gagnez une boule de neige qui vous téléporte à l'endroit où elle tombe.");
			
			for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
				gamePlayer.getPlayer().getInventory().addItem(ItemManager.makeBasicItem(Material.SNOWBALL, nameSnowball, 1));
			}
		}
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of("§fBonus Élémentaire : §7"+ Element.EAU.getName() + " | " + Element.AIR.getName()));
		
		ItemStack item = new ItemStack(Material.BLUE_ICE, 1);
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
