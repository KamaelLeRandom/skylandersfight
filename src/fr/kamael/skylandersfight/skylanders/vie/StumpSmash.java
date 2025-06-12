package fr.kamael.skylandersfight.skylanders.vie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.vie.entity.StumpSmashFireball;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class StumpSmash extends Skylander {
	public static final String name = "Stump Smash";
	
	public static final String nameWeapon = "§2Marto";
	public static final String namePassif = "§2Sève Vitale";
	public static final Double healPassif = 2.;
	public static final Integer numberMaxPassif = 2;
	
	public static final String nameFirstSpell = "§2Écrasement";
	public static final Integer timerFirstSpell = 20;
	public static final Double damageFirstSpell = 6.;
	public static final Double rangeFirstSpell = 3.5;
	
	public static final String nameSecondSpell = "§2Orbe Toxique";
	public static final Integer timerSecondSpell = 30;
	public static final Integer secDurationPoisonSecondSpell = 8;
	public static final Double rangeDetectPoisonSecondSpell = 3.;
	
	private Integer numberOfPassif = 0;
	
	public StumpSmash(Player player) {
		super(player, Element.VIE, name);
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
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (numberOfPassif < numberMaxPassif) {
			numberOfPassif++;
			player.getInventory().addItem(getItemPassif());
		}
		
		return false; 
	}
	
	public void passif_Heal() {
		if (numberOfPassif > 0) {
			numberOfPassif--;
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			
			if (itemInHand.getType().equals(Material.LIME_DYE)) {
				ItemManager.removeAmount(itemInHand, -1);
				SpellUtils.heal(this, healPassif, true);
			}
		}
	}
	
 	public void firstSpell_Damage() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ nameFirstSpell +"§f.");
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeFirstSpell, 2., rangeFirstSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez de vous faire écraser par la compétence "+ nameFirstSpell +"§f de §a"+ player.getName() +"§f.");
				playerHit.damage(damageFirstSpell, player);
				if (!playerHit.getLocation().getBlock().getType().isSolid())  
					playerHit.teleport(playerHit.getLocation().clone().subtract(0, 1, 0));
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Poison() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ nameSecondSpell +"§f.");
			
			new StumpSmashFireball(this, player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(2)));
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, pour obtenir une "+ namePassif +" vous devez infligez un dégat au corps à corps, en l'utilisant vous êtes régénéré de §c"+ healPassif +"<3§f. ("+ numberMaxPassif +" cumulable à la fois)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous écraser les joueurs autour de vous (- de "+ rangeFirstSpell +" blocs) ce qui inflige §a"+ damageFirstSpell +" dégats§f et les §aenterre d'un bloc§f. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous lancez une boule empoisonné qui §aexplose au contact d'un joueur§f (- de "+ rangeDetectPoisonSecondSpell +" blocs) qui applique l'§aeffet Poison§f pendant "+ secDurationPoisonSecondSpell +" secondes. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§2"+ name +"§f est un Skylander §cmélée§f capable de soigner");
		lore.add("§fassez facilement.");
		ItemStack item = new ItemStack(Material.OAK_LOG, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§2"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getItemPassif() {
		List<String> lore = Arrays.asList(
			"§fVous pouvez utiliser cette objet pour vous soignez de §c"+ healPassif/2 +"❤️§f. (max "+ numberMaxPassif +" cumulable)"
		);
		ItemStack item = new ItemStack(Material.LIME_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(namePassif);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous écrasez les joueurs proche de vous (- de "+ rangeFirstSpell +" blocs) ce qui infligie "+ damageFirstSpell +" dégats et enterre d'un bloc sous terre."
		);
		ItemStack item = new ItemStack(Material.ANVIL, 1);
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
			"§fVous lancez une boule empoisonné qui §aexplose au contact d'un joueur§f (- de "+ rangeDetectPoisonSecondSpell +" blocs) qui applique l'§aeffet Poison§f pendant "+ secDurationPoisonSecondSpell +"s."
		);
		ItemStack item = new ItemStack(Material.SPIDER_EYE, 1);
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
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
}
