package fr.kamael.skylandersfight.skylanders.vie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class StealthElf extends Skylander {
	public static final String name = "Stealth Elf";
	
	public static final Double pourcentHealPassif = 0.1;
	
	public static final String nameFirstSpell = "§2Camouflage";
	public static final Integer timerFirstSpell = 30;
	public static final Integer durationInviFirstSpell = 15;
	public static final Integer tickPoisonFirstSpell = 100;
	
	public static final String nameSecondSpell = "§2Substitution";
	public static final Integer timerSecondSpell = 30;
	public static final Integer rangeSecondSpell = 10;
	public static final Integer durationCloneSecondSpell = 3;
	public static final Double damageCloneSecondSpell = 5.;

	private Boolean applyPoison = false;
	
	public StealthElf(Player player) {
		super(player, Element.VIE, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.GREEN);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (applyPoison) {
			applyPoison = false;
			skylanderHit.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, tickPoisonFirstSpell, 1, false, false));
		}
		
		SpellUtils.heal(this, damage * pourcentHealPassif, false);
		
		return damage; 
	}
	
	public void firstSpell_Invi() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ nameFirstSpell +"§f.");
			applyPoison = true;
			
			SpellUtils.invisibility(plugin, this, durationInviFirstSpell*20);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Dash() {
		if (checkCooldown(nameSecondSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(this, rangeSecondSpell, null);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				
				Location oldLocation = player.getLocation().clone();
				
				SpellUtils.createFakePlayer(
					plugin,
					this,
					playerTarget,
					durationCloneSecondSpell,
					(attacker, targer) -> {
						for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, oldLocation, 5., 2., 5.)) {
							Player playerHit = skylanderHit.getPlayer();
							playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
							playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par l'explosion du clone de §f"+ player.getName() +"§f.");
							playerHit.damage(damageCloneSecondSpell, player);
						}
						
						oldLocation.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, oldLocation, 1, 0., 0., 0.);
					}
				);
				
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ nameSecondSpell +"§f sur §a"+ playerTarget.getName() +"§f.");
				player.teleport(playerTarget.getLocation().subtract(playerTarget.getLocation().getDirection()));
				
				addCooldown(nameSecondSpell, timerSecondSpell);
			}
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ §2"+ name +"§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ §2Vol-Vie§f, vous êtes §asoigné§f d'un montant égal à §a" + pourcentHealPassif*100 + "%§f de vos §adégâts infligés§f.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous devenez §ainvisible§f pendant §a" + durationInviFirstSpell + " secondes§f, votre §aprochaine attaque§f inflige un effet de §apoison§f de §a" + SkylanderConverter.convertTicks(tickPoisonFirstSpell) + " secondes§f à la cible. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous êtes §atéléporté§f dans le §ados du joueur visé§f, vous laissez derrière vous un §aclone§f pendant §a" + durationCloneSecondSpell + " secondes§f. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§2"+ name +"§f est un Skylander §cmélée§f maîtrisant");
		lore.add("§fdes techniques typique des assassins.");
		
		ItemStack item = new ItemStack(Material.ARMOR_STAND, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§2"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous devenez §2invisible§f pendant §2"+ SkylanderConverter.convertTicks(durationInviFirstSpell) +"s§f,", "§fvotre première attaque §2empoissone§f la cible pendant §2"+ SkylanderConverter.convertTicks(tickPoisonFirstSpell) +"s§f.");
		
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
		List<String> lore = Arrays.asList("§fVous êtes §2téléporté§f dans le §2dos du joueur visé§f,", "§fvous laissez derrière vous un §2clone§f pendant "+ durationCloneSecondSpell +"s§f.");
		
		ItemStack item = new ItemStack(Material.ARMOR_STAND, 1);
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
		meta.setDisplayName("§2Dague");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
