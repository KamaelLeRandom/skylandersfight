package fr.kamael.skylandersfight.skylanders.eau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class SlamBam extends Skylander {
	public static final String name = "Slam Bam";
	
	public static final String namePassif = "§9Poing de Yéti";
	public static final Double damagePassif = 2.5;
	
	public static final String nameFirstSpell = "§9Stéroïdes";
	public static final Integer timerFirstSpell = 20;
	public static final Integer numberHitPassif = 12;
	
	public static final String nameSecondSpell = "§9Gonflette";
	public static final Integer timerSecondSpell = 20;
	public static final Integer durationSecondSpell = 8;
	public static final Double pourcentResisSecondSpell = 0.25;
	
	public Integer nbHitPassif = numberHitPassif;
	
	public SlamBam(Player player) {
		super(player, Element.EAU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.BLUE);
				
		player.setLevel(nbHitPassif);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Boolean applyEnemyResistance() { 
		return false; 
	}
	
	public Double addDamage(Double damage, Skylander skylanderHit) { 
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {	
			return damage + damagePassif;
		}
		
		return damage; 
	}
	
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR) && nbHitPassif > 0) {	
	        new BukkitRunnable() {
	            @Override
	            public void run() {
	    			skylanderHit.getPlayer().setNoDamageTicks(skylanderHit.getPlayer().getMaximumNoDamageTicks() / 4);
	    			nbHitPassif--;
	    			player.setLevel(nbHitPassif);
	            }
	        }.runTaskLater(plugin, 1);
		}
		
		return false; 
	}
	
	public void firstSpell_Reset() {
		if (checkCooldown(nameFirstSpell, true)) {
			nbHitPassif = numberHitPassif;
			
			player.playSound(player.getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
			player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 20, 0, false, false));
			player.setLevel(nbHitPassif);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Resis() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
			player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, false, false));
			resis -= pourcentResisSecondSpell;
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
						player.sendMessage(Constants.prefixMessage+ "Votre compétence "+ nameSecondSpell +"§f vient de prendre fin.");
						player.removePotionEffect(PotionEffectType.SLOW);
						resis += pourcentResisSecondSpell;
						cancel();
						return;
					}
				
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§9" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, vous frappez vos adversaires au pied ce qui vous permet de travers de la §3Résistance§f de l'adversaire.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, lors de vos §3"+ numberHitPassif +"§f prochain coups, l'adversaire n'a plus de délai d'§3invulnérabilité§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous gagnez §3"+ (pourcentResisSecondSpell*100) +"% de Résistance§3 pendant §3"+ durationSecondSpell +" secondes§f, cependant vous êtes grandement §cralenti§f. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§9"+ name +"§f est un Skylander §cmélée§f ayant la");
		lore.add("§fcapacité de frappé très vite et très fort.");
		
		ItemStack item = new ItemStack(Material.BLUE_ICE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§9"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fLors de vos "+ numberHitPassif + " prochain coups,", "§fl'adversaire n'a plus de délai d'invulnérabilité.");
		
		ItemStack item = new ItemStack(Material.SUGAR, 1);
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
		List<String> lore = Arrays.asList("§fVous gagnez §3"+ (pourcentResisSecondSpell*100) +"%§f de Résistance", "§fpendant "+ durationSecondSpell +" secondes, cependant", "§fvous êtes §cralenti§f et ne pouvez §cplus sauter§f.");
		
		ItemStack item = new ItemStack(Material.BLUE_DYE, 1);
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
}
