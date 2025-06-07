package fr.kamael.skylandersfight.skylanders.eau;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class WhamShell extends Skylander {
	public static final String name = "Wham Shell";
	
	public static final String nameWeapon = "§9Masse";
	public static final String namePassif = "§9Armure Épineuse";
	public static final Double damagePassif = 2.;
	
	public static final String nameFirstSpell = "§9Étoiles de mer";
	public static final Integer timerFirstSpell = 15;

	public static final String nameSecondSpell = "§9Déchargement";
	public static final Integer timerSecondSpell = 30;
	public static final Integer durationBlindSecondSpell = 5;
	public static final Double rangeSecondSpell = 5.;

	private Boolean firstSpellActived = false;
	
	public WhamShell(Player player) {
		super(player, Element.EAU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
					
		player.setWalkSpeed(0.23f);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	@Override
	public Boolean onHitSword(Skylander skylanderDamager) { 
		skylanderDamager.getPlayer().damage(damagePassif, player);
		return false; 
	}
	
	public void firstSpell_SwapWeapon() {
		if (firstSpellActived) {
			firstSpellActived = false;
			player.playSound(player.getLocation(), Sound.ITEM_DYE_USE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de changer d'arme, vous avez obtenu votre arme mélée");
			player.getInventory().setItem(player.getInventory().first(getItemBow()), getItemWeapon());
			player.updateInventory();
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
		
		if (checkCooldown(nameFirstSpell, true)) {
			firstSpellActived = true;
			player.playSound(player.getLocation(), Sound.ITEM_DYE_USE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de changer d'arme, vous avez obtenu votre arme de distance.");
			player.getInventory().setItem(player.getInventory().first(getItemWeapon()), getItemBow());
			player.updateInventory();
			return;
		}
	}
	
	public void secondSpell_Blindness() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_GLOW_SQUID_SQUIRT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			player.getWorld().strikeLightningEffect(player.getLocation());
			
			ParticleUtils.sphereParticule(plugin, player.getLocation(), Particle.CRIT, rangeSecondSpell);
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GLOW_SQUID_SQUIRT, 1, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence de " + nameSecondSpell + "§f de §b" + player.getName() + "§f.");
				playerHit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationBlindSecondSpell * 20, 0, false, false));
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
		player.sendMessage("≫ "+ namePassif +"§f, .");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous §baveuglez§f pendant "+ durationBlindSecondSpell +" secondes tout les adversaires qui sont à moins de " + rangeSecondSpell + " blocs de vous. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§9"+ name +"§f est un Skylander §cmélée§f ayant la");
		lore.add("§fcapacité de passer de mélée à distance.");
		ItemStack item = new ItemStack(Material.NAUTILUS_SHELL, 1);
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
			"§fVous pouvez activer et désactiver cette compétence, afin de changer votre armé mélée en arme distance."
		);
		ItemStack item = new ItemStack(Material.COMPARATOR, 1);
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
			"§fVous §9aveuglez§f pendant §b" + durationBlindSecondSpell + "s§f tout les joueurs à moins de " + rangeSecondSpell + " blocs de vous."
		);
		ItemStack item = new ItemStack(Material.YELLOW_DYE, 1);
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
	
	public static ItemStack getItemBow() {
		ItemStack item = new ItemStack(Material.BOW, 1);
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
