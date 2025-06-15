package fr.kamael.skylandersfight.skylanders.eau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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
	public static final Integer durationFirstSpell = 8;
	public static final Double pourcentForceFirstSpell = 0.25;

	
	public static final String nameSecondSpell = "§9Contraction";
	public static final Integer timerSecondSpell = 20;
	public static final Integer durationSecondSpell = 8;
	public static final Double pourcentResisSecondSpell = 0.25;
	public static final Double valueKnockbackResisSecondSpell = 0.8;
		
	public SlamBam(Player player) {
		super(player, Element.EAU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
				
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Element.eauSpeed);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Double addDamage(Double damage, Skylander skylanderHit) { 
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {	
			return damage + damagePassif;
		}
		
		return damage; 
	}
	
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {	
			new BukkitRunnable() {
				@Override
		        public void run() {
					skylanderHit.getPlayer().setNoDamageTicks(skylanderHit.getPlayer().getMaximumNoDamageTicks() / 4);
		    		cancel();
		    		return;
				}
		    }.runTaskLater(plugin, 1);		
		}
		
		return false; 
	}
	
	public void firstSpell_Force() {
		if (checkCooldown(nameFirstSpell, true)) {			
			player.playSound(player.getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
			player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			force += pourcentForceFirstSpell;
			
			new BukkitRunnable() {
				private Integer timer = durationFirstSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
						player.sendMessage(Constants.prefixMessage+ "Votre compétence "+ nameFirstSpell +"§f vient de prendre fin.");
						force -= pourcentForceFirstSpell;
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Resis() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
			player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, false, false));
			player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(valueKnockbackResisSecondSpell);
			resis -= pourcentResisSecondSpell;
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
						player.sendMessage(Constants.prefixMessage+ "Votre compétence "+ nameSecondSpell +"§f vient de prendre fin.");
						player.removePotionEffect(PotionEffectType.SLOW);
						player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(Constants.baseKnockback);
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
		player.sendMessage("≫ "+ namePassif +"§f, lorsque vous frappez à la main, vous annulez le délai d'invulnérabilité du joueur touché, vous permettant de faire des gros combos.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous gagnez §6"+ (pourcentForceFirstSpell*100) +"%§f de §cForce§f pendant "+ durationFirstSpell +" secondes. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous gagnez §6"+ (pourcentResisSecondSpell*100) +"%§f de §cRésistance§f et réduisez votre §eknockback§f de §6"+ valueKnockbackResisSecondSpell*100 +"%§f pendant §b"+ durationSecondSpell +" secondes§f, cependant vous êtes grandement §cralenti§f durant cette période. §b(" + timerSecondSpell + "s de recharge)");
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
		List<String> lore = Arrays.asList(
			"§f."
		);
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
		List<String> lore = Arrays.asList(
			"§f."
		);
		ItemStack item = new ItemStack(Material.IRON_INGOT, 1);
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
