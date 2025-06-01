package fr.kamael.skylandersfight.skylanders.tech.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class DrobotDroid extends Skylander {
	public static String name = "D-roid";
	public static String nameWeapon = "§eLame Laser";
	
	public DrobotDroid(Player player) {
		super(player, Element.TECH, name);
		this.force = 1.05;
		this.resis = 0.95;
	}

	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.YELLOW);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemWeapon());
	}
	
	public static ItemStack getItemWeapon() {
		ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		return item;
	}
}
