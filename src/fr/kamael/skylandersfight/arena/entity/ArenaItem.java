package fr.kamael.skylandersfight.arena.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class ArenaItem extends CustomEntity {
	private ItemStack item;
	
	public ArenaItem(Location location) {
		super(null, location);
		
		this.item = getRandomItem();
	}
	
	public void summon() { 		
		ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setCanPickupItems(false);
		as.setCustomName("§eObjet Aléatoire");
		as.setCustomNameVisible(true);
		as.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 1, false, false));
		as.getEquipment().setHelmet(new ItemStack(Material.BEACON));
		this.entity = as;
		
		return;
	}
	
	public void onHit(Skylander skylander) {
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
		player.getInventory().addItem(item);
		removeEntity();
		return; 
	}
	
	public ItemStack getRandomItem() {
		Integer idx = plugin.random.nextInt(6);
		
		switch (idx) {
			case 0:
				return getItemEon();
			case 1:
				return getItemFlynn();
			case 2:
				return getItemHugo();
			case 3: 
				return getItemCali();
			case 4:
				return getItemKaos();
			case 5:
				return getItemGlumshanks();
			default:
				return null;
		}
	}
	
	private ItemStack getItemEon() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous êtes §csoigné§7 de §c10 coeurs§7.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameEon);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
	
	private ItemStack getItemFlynn() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous §cretéléportez§7 tout les joueurs §caléatoirement§7.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameFlynn);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
	
	private ItemStack getItemHugo() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous obtenez les §cinformations§7 de tout les joueurs.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameHugo);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
	
	private ItemStack getItemCali() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous gagnez §c10%§7 de §cForce§7 ou §cRésistance§7.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameCali);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
	
	private ItemStack getItemKaos() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous §cempoissonez§7 le joueur visé.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameKaos);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
	
	private ItemStack getItemGlumshanks() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Vous §creduisez§7 de 10% la §cForce§7 et §cRésistance§7 du joueur visé.");
		
		ItemStack it = new ItemStack(Material.BEACON, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(Constants.itemNameGlumshank);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;
	}
}
