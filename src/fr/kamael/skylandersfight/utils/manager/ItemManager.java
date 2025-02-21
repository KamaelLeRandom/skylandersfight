package fr.kamael.skylandersfight.utils.manager;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemManager {
	
	@SuppressWarnings("deprecation")
	public static void clearPlayer(Player player) {
		for (PotionEffect potion : player.getActivePotionEffects()) {
			player.removePotionEffect(potion.getType());
		}
		
		player.getInventory().clear();
		player.getInventory().setBoots(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setHelmet(null);
		player.setMaxHealth(20);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.setFlying(false);
		player.setBedSpawnLocation(null);
		player.setSaturation(99999);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
	}
	
	public static void giveColorArmor(Player player, Color color) {
		player.getInventory().setHelmet(makeColorArmor(Material.LEATHER_HELMET, color));
		player.getInventory().setChestplate(makeColorArmor(Material.LEATHER_CHESTPLATE, color));
		player.getInventory().setLeggings(makeColorArmor(Material.LEATHER_LEGGINGS, color));
		player.getInventory().setBoots(makeColorArmor(Material.LEATHER_BOOTS, color));
	}
	
	public static ItemStack makeColorArmor(Material material, Color color) {
		ItemStack it = new ItemStack(material, 1);
		LeatherArmorMeta itM = (LeatherArmorMeta) it.getItemMeta();
		itM.setColor(color);
		itM.setUnbreakable(true);
		it.setItemMeta(itM);
		return it;
	}
	
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
