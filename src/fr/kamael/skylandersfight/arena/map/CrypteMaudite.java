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

public class CrypteMaudite extends Arena {
	public static final String nameArena = "§7Crypte Maudite";

	public CrypteMaudite() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -855, 27 ,180));
		this.playerSpawns.add(new Location(w, -880.5, 22, 199.5));
		this.playerSpawns.add(new Location(w, -911, 22, 178));
		this.playerSpawns.add(new Location(w, -856, 24, 148));
		this.playerSpawns.add(new Location(w, -912, 22, 148));
		this.playerSpawns.add(new Location(w, -851, 27, 208));
		
		this.healSpawns.add(new Location(w, -845, 28, 207));
		this.healSpawns.add(new Location(w, -845, 28, 199));
		this.healSpawns.add(new Location(w, -850, 23, 156));
		this.healSpawns.add(new Location(w, -893, 25, 151));
		this.healSpawns.add(new Location(w, -912, 20, 191));
		this.healSpawns.add(new Location(w, -881, 26, 198));
		this.healSpawns.add(new Location(w, -915, 22, 210));
		
		this.itemSpawns.add(new Location(w, -864.5, 23, 161.5));
		this.itemSpawns.add(new Location(w, -896.5, 27, 147.5));
		this.itemSpawns.add(new Location(w, -887.5, 21, 178.5));
		this.itemSpawns.add(new Location(w, -846.5, 26, 210.5));
		this.itemSpawns.add(new Location(w, -880.5, 21, 202.5));
		this.itemSpawns.add(new Location(w, -914.5, 21, 198.5));
		
		this.elements.add(Element.MORT);
		this.elements.add(Element.TERRE);
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
			"§7Bonus Élémentaire : " + Element.MORT.getName() + " §7| " + Element.TERRE.getName()
		));

		ItemStack item = new ItemStack(Material.OAK_PLANKS, 1);
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
