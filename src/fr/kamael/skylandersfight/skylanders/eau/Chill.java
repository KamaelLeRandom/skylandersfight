package fr.kamael.skylandersfight.skylanders.eau;

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
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.eau.entity.ChillFish;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Chill extends Skylander {
	public static final String name = "Chill";
	
	public static final String nameWeapon = "§9Javelot glacée";
	public static final String namePassif = "§9Brise-glace";
	public static final Double ratioDamagePassif = 1.25;
	
	public static final String nameFirstSpell = "§9Narval";
	public static final Integer timerFirstSpell = 20;
	public static final Double rangeFirstSpell = 1.;
	public static final Double damageFirstSpell = 10.;
	
	public static final String nameSecondSpell = "§9Glaciation";
	public static final Integer timerSecondSpell = 20;
	public static final Double rangeSecondSpell = 5.;
	public static final Integer tickFreezeSecondSpell = 60;
	
	public Chill(Player player) {
		super(player, Element.EAU, name);
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
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (skylanderHit.checkStatus(Status.FREEZE))
			return damage * ratioDamagePassif;
		return damage; 
	}
	
	public void firstSpell_Fish() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			
			new ChillFish(this, player.getLocation());
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Freeze() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence " + nameSecondSpell + " de §b" + player.getName() + "§f.");
				playerHit.sendTitle(nameSecondSpell, "§7Gélé pendant " + SkylanderConverter.convertTicks(tickFreezeSecondSpell), 2, tickFreezeSecondSpell, 2);
				addStatus(tickFreezeSecondSpell, Status.FREEZE);
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ " + element.getColor() + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, vous infligez §b" + ratioDamagePassif *100 + "%§f de vos dégats lorsque vous frappez un §bjoueur gélé§f.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous envoyez un §bpoisson§f qui inflige §b" + damageFirstSpell + " dégats§f si celui-ci passe proche d'un joueur. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous §bgelez§f les joueurs ennemies qui sont à moins de " + rangeSecondSpell + " blocs de vous. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§9"+ name +"§f est un Skylander §cmélée§f ayant la");
		lore.add("§fcapacité de gelez ses adversaires afin d'augmenter ses dégats.");
		ItemStack item = new ItemStack(Material.COD, 1);
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
			"§fVous envoyez un poisson qui inflige " + damageFirstSpell + " dégats aux joueurs proches."
		);
		ItemStack item = new ItemStack(Material.COD, 1);
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
			"§fVous gelez tout les joueurs ennemies à moins de " + rangeSecondSpell + " blocs de vous.", 
			"§f(les joueurs gélés peuvent ni bouger ni attaquer)"
		);
		ItemStack item = new ItemStack(Material.ICE, 1);
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
