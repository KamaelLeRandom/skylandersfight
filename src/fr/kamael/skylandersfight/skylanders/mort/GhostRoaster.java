package fr.kamael.skylandersfight.skylanders.mort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.TraversableBlocksUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class GhostRoaster extends Skylander {
	public static final String name = "Ghost Roaster";
	
	public static final String namePassif = "§7Revanche";
	public static final Integer durationPassif = 10;
	
	public static final String nameFirstSpell = "§7Cape Spectrale";
	public static final Integer timerFirstSpell = 5;
	public static final Double removeHealFirstSpell = 4.;
	public static final Integer ticksInvulFirstSpell = 50;
	
	public static final String nameSecondSpell = "§7Passe-Blocs";
	public static final Integer timerSecondSpell = 5;
	public static final Double removeHealSecondSpell = 6.;
	public static final Integer tickSpectralSecondSpell = 100;
	
	public Boolean passifActive = false;
	public Boolean passifAvailable = true;
	
	public GhostRoaster(Player player) {
		super(player, Element.MORT, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	@Override
	public Boolean onKill(Skylander skylanderDeath) { 
		if (passifActive) {
			passifActive = false;
			passifAvailable = true;
			
			player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de ressuciter grâce à votre compétence "+ namePassif +"§f.");
			
			giveEquipement();
			setFullHealth();
			
			removeStatus(Status.NOTAKEDAMAGE);
		}
		
		return false; 
	}
	
	@Override
	public Boolean onDeath(Skylander skylanderKill) { 
		if (passifAvailable) {
			passifActive = true;
			passifAvailable = false;
			
			Location oldLocation = player.getLocation();
			
			player.spigot().respawn();
			player.teleport(oldLocation);
			player.getInventory().clear();
			player.getInventory().setHelmet(ItemManager.makeBasicItem(Material.NETHERITE_HELMET, "§7-", 1));
			player.getInventory().setChestplate(ItemManager.makeBasicItem(Material.NETHERITE_CHESTPLATE, "§7-", 1));
			player.getInventory().setLeggings(ItemManager.makeBasicItem(Material.NETHERITE_LEGGINGS, "§7-", 1));
			player.getInventory().setBoots(ItemManager.makeBasicItem(Material.NETHERITE_BOOTS, "§7-", 1));
			player.getInventory().addItem(getItemWeapon());
			
			addStatus(null, Status.NOTAKEDAMAGE);
			
			new BukkitRunnable() {
				private Integer timer = durationPassif;
				
				@Override
				public void run() {
					if (!passifActive) {
						cancel();
						return;
					}
					
					if (timer == 0 || !plugin.game.isState(GameState.FIGHTING)) {
						removeStatus(Status.NOTAKEDAMAGE);
						player.setHealth(0);
						cancel();
						return;
					}
					
					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
					player.sendTitle(namePassif, "§f" + timer + "s restantes", 2, 16, 2);
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			return true;
		}
		
		return false;
	}
	
	public void firstSpell_Invul() {
		if (checkCooldown(nameFirstSpell, true)) {
			
			if (SpellUtils.changeLife(this, -removeHealFirstSpell)) {
				player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
				
				SpellUtils.invulnerability(plugin, this, ticksInvulFirstSpell);
				
				addCooldown(nameFirstSpell, timerFirstSpell);
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous n'avez plus assez de points de vie pour utiliser "+ nameFirstSpell +"§f.");
			}
		}
	}
	
	public void secondSpell_Spectral() {
		if (checkCooldown(nameSecondSpell, true)) {
			
			if (SpellUtils.changeLife(this, -removeHealSecondSpell)) {
				if (checkStatus(Status.NOTELEPORT)) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez ne pouvez §cpas utiliser§f de compétence de §ctéléportation§f pour le moment.");
					return;
				}
				
				Location oldLocation = player.getLocation();
				
				player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
				player.setGameMode(GameMode.SPECTATOR);
				
				new BukkitRunnable() {
					private Integer timer = tickSpectralSecondSpell;
					@Override
					public void run() {
						if (timer == 0) {
							player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 1, 1);
							player.sendMessage(Constants.prefixMessage + nameSecondSpell + "§f est terminé, vous êtes de retour à votre position initial.");
							player.setGameMode(GameMode.ADVENTURE);
							player.teleport(oldLocation);
							cancel();
							return;
						}
						
						if (timer <= 80 && TraversableBlocksUtils.isTraversableBlock(player.getLocation().getBlock().getType()) && TraversableBlocksUtils.isTraversableBlock(player.getEyeLocation().getBlock().getType())) {
							player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 1, 1);
							player.sendMessage(Constants.prefixMessage + nameSecondSpell + "§f est terminé, vous êtes de nouveau tangible.");
							player.setGameMode(GameMode.ADVENTURE);
							cancel();
							return;
						}
						
						timer--;
					}
				}.runTaskTimer(plugin, 0, 1);
				
				addCooldown(nameSecondSpell, timerSecondSpell);
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous n'avez plus assez de points de vie pour utiliser "+ nameSecondSpell +"§f.");
			}
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ §7"+ name +"§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, lorsque vous mourrez, vous avez "+ durationPassif +" secondes pour vous vengez afin d'être ressuciter§f.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous devenez §3invulnérable§f pendant §3" + SkylanderConverter.convertTicks(ticksInvulFirstSpell) + " secondes§f, cependant vous §cperdez "+ removeHealFirstSpell/2 +" coeurs permanents§f. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous devenez §3intangible§f pendant §3" + SkylanderConverter.convertTicks(tickSpectralSecondSpell) + " secondes§f, vous permettant de traverser les murs, cependant vous §cperdez "+ removeHealSecondSpell/2 +" coeurs permanents§f. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7"+ name +"§f est un Skylander §cmélée§f maîtrisant");
		lore.add("§fdes capacité digne d'un fantôme.");
		
		ItemStack item = new ItemStack(Material.PHANTOM_MEMBRANE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous devenez §7invulnérable§f pendant §7"+ SkylanderConverter.convertTicks(ticksInvulFirstSpell) +"s§f,", "cependant vous §cperdez "+ removeHealFirstSpell/2 +" coeurs permanents§f.");
		
		ItemStack item = new ItemStack(Material.PHANTOM_MEMBRANE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameFirstSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemSecondSpell() {
		List<String> lore = Arrays.asList("§fVous devenez intangible vous permettant de", "§ftraverser les murs pendant §7"+ SkylanderConverter.convertTicks(tickSpectralSecondSpell) +"s§f.");
		
		ItemStack item = new ItemStack(Material.GLASS, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack getItemWeapon() {
		ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7Boulet");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
