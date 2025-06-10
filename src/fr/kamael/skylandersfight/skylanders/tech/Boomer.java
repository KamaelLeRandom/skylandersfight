package fr.kamael.skylandersfight.skylanders.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.entity.BoomerTNT;
import fr.kamael.skylandersfight.skylanders.tech.entity.BoomerTrapTNT;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Boomer extends Skylander {
	public static final String name = "Boomer";
	
	public static final String nameWeapon = "§eTNT";
	public static final String namePassif = "§eLancement de TNT";
	public static final Integer timerPassif = 20;
	public static final Double rangeThrowPassif = 0.5;
	public static final Double rangeExplosePassif = 2.5;
	public static final Double damagePassif = 2.;
	
	public static final String nameFirstSpell = "§eDynamite";
	public static final Integer timerFirstSpell = 30;
	public static final Double rangeTrapFirstSpell = 1.0;
	public static final Double damageTrapFirstSpell = 5.;
	
	public static final String nameSecondSpell = "§eKamikaze";
	public static final Integer timerSecondSpell = 10;
	public static final Double rangeSecondSpell = 5.;
	public static final Double ratioVectorSecondSpell = 1.5;
	
	private Boolean canUsePassif = true;
	
	public Boomer(Player player) {
		super(player, Element.TECH, name);
		this.force = 1. + Element.techForce;
		this.resis = 1. - Element.techResis;
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
	
	public void passif_ThrowTNT() {
		if (canUsePassif) {
			canUsePassif = false;
			
			new BoomerTNT(this, player.getLocation());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					canUsePassif = true;
					cancel();
					return;
				}
			}.runTaskLater(plugin, timerPassif);
			return;
		}
	}
	
	public void firstSpell_Trap() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ARMOR_STAND_PLACE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameFirstSpell + "§f.");
			
			new BoomerTrapTNT(this, player.getLocation());
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Explosion() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			player.spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				
				Vector direction = playerHit.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
				Vector knockback = direction.multiply(ratioVectorSecondSpell);
				knockback.setY(0.5);
				
				playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				playerHit.sendMessage("Vous venez d'être touché par la compétence " + nameSecondSpell + "§f de §6" + player.getName() + "§f.");
				playerHit.setVelocity(knockback);
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶" + element.getColor() + name + "§f◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, vous pouvez lancer des explosives avec votre "+ nameWeapon +"§f qui explose infligeant §6"+ damagePassif +" dégats§f au contact d'un joueur ou après certain temps. §b("+ SkylanderConverter.convertTicks(timerPassif) +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous posez §6un piège§f qui explose si un joueur passe proche infligeant §6" + damageTrapFirstSpell + " dégats§f. §b("+ timerFirstSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous explosez ce qui envoie à l'opposé de vous tout les adversaires qui sont à moins de " + rangeSecondSpell + " blocs de vous. §b("+ timerSecondSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§e"+ name +"§f est un Skylander §cmélée§f capable de pièger");
		lore.add("§fle terrain avec ses diverses explosifs.");
		ItemStack item = new ItemStack(Material.TNT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous placez une dynamite au sol qui explose si un joueur passe proche de celle-ci."
		);
		ItemStack item = new ItemStack(Material.REDSTONE, 1);
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
			"§fVous explosez ce qui envoie tout les joueurs proche de vous en l'air."
		);
		ItemStack item = new ItemStack(Material.GUNPOWDER, 1);
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
		ItemStack item = new ItemStack(Material.TNT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
		return item;
	}
}
