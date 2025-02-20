package fr.kamael.skylandersfight.utils.manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
	
	public static ItemStack getInventoryGlass() {
		ItemStack it = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName("§7-");
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it.setItemMeta(itM);
		return it;
	}
	
	public static ItemStack makeBasicItem(Material material, String name, Integer number) {
		ItemStack it = new ItemStack(material, number);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(name);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it.setItemMeta(itM);
		return it;
	}
}
