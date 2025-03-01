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
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;

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
		
		plugin.game.getPlayer(player).getStats().nbItem++;
		
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
	
	public static ItemStack getItemEon() {
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
	
	public static void spellEon(Plugin plugin, Skylander skylander) {
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ Constants.itemNameEon + "§f.");
		
		new BukkitRunnable() {
			private Integer time = 10;
			
			@Override
			public void run() {
				if (time == 0) {
					cancel();
					return;
				}
				
				SpellUtils.heal(skylander, 1., true);
				
				time--;
			}
		}.runTaskTimer(plugin, 0, 10);
	}
	
	public static ItemStack getItemFlynn() {
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
	
	public static void spellFlynn(Plugin plugin, Skylander skylander) {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Skylander skylanderOther = gamePlayer.getSkylander();
			Player playerOther = gamePlayer.getPlayer();
			
			if (skylander.isAlive() && !skylander.equals(skylanderOther)) {
				playerOther.playSound(playerOther.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
				playerOther.sendMessage(Constants.prefixMessage + "Vous venez d'être téléporté aléatoirement par la "+ Constants.itemNameFlynn);
				playerOther.teleport(plugin.game.getRound().getArena().getRandomPlayerSpawn());
			}
		}
		
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez de retéléporté aléatoirement tout les joueurs grâce à votre "+ Constants.itemNameFlynn);
	}
	
	public static ItemStack getItemHugo() {
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
	
	public static void spellHugo(Plugin plugin, Skylander skylander) {
		String msg = "§8§l===== "+ Constants.itemNameHugo +" §8=====\n";
		
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Skylander skylanderOther = gamePlayer.getSkylander();
			Player playerOther = gamePlayer.getPlayer();
			
			if (!skylander.equals(skylanderOther)) {
				msg += "§7➤§e" + playerOther.getName() + "§f (§7" + skylanderOther.getName() + "§f) : " + Math.round(playerOther.getHealth()/2) + " §c❤§f | " + SkylanderConverter.convertForce(skylanderOther.getForce()) + "% §c🗡§f | " + SkylanderConverter.convertResis(skylanderOther.getResis()) + "% §c🛡§f |.\n";
			}
		}
		
		msg += "§8================================";
		
		skylander.getPlayer().sendMessage(msg);
	}
	
	public static ItemStack getItemCali() {
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
	
	public static void spellCali(Plugin plugin, Skylander skylander) {
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre " + Constants.itemNameCali + "§f.");
		
		if (plugin.random.nextBoolean()) {
			skylander.updateForce(+0.1);
		} else {
			skylander.updateResis(-0.1);
		}
	}
	
	public static ItemStack getItemKaos() {
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
	
	public static Boolean spellKaos(Plugin plugin, Skylander skylander) {
		Skylander skylanderTarget = SpellUtils.targetPlayer(skylander, 15, null);
		Player player = skylander.getPlayer();
		
		if (skylanderTarget == null) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
			return false;
		} else {
			Player playerTarget = skylanderTarget.getPlayer();
			playerTarget.playSound(playerTarget.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
			playerTarget.sendMessage(Constants.prefixMessage + "Vous avez été touché par la "+ Constants.itemNameKaos + "§f de §e"+ player.getName() + "§f.");
			playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 2, false, false));
			
			player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous avez touché §e"+ playerTarget.getName() +"§f avec votre "+ Constants.itemNameKaos + "§f.");
			return true;
		}
	}
	
	public static ItemStack getItemGlumshanks() {
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
	
	public static Boolean spellGlum(Plugin plugin, Skylander skylander) {
		Skylander skylanderTarget = SpellUtils.targetPlayer(skylander, 15, null);
		Player player = skylander.getPlayer();
		
		if (skylanderTarget == null) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
			return false;
		} else {
			Player playerTarget = skylanderTarget.getPlayer();
			playerTarget.playSound(playerTarget.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
			playerTarget.sendMessage(Constants.prefixMessage + "Vous avez été touché par la "+ Constants.itemNameGlumshank + "§f de §e"+ player.getName() + "§f.");
			skylanderTarget.updateForce(-0.1);
			skylanderTarget.updateResis(+0.1);
			
			player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous avez touché §e"+ playerTarget.getName() +"§f avec votre "+ Constants.itemNameGlumshank + "§f.");
			return true;
		}
	}
}
