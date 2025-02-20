package fr.kamael.skylandersfight.utils.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemManager {
	
	@SuppressWarnings("deprecation")
	public static ItemStack createPlayerSkull(Player p) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(p.getName());
		skull.setItemMeta(meta);
		return skull;
	}
}
