package fr.kamael.skylandersfight.skylanders.terre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.TraversableBlocksUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Terrafin extends Skylander {
	public static final String name = "Terrafin";
	
	public static final String namePassif = "§6Écrasement";
	public static final Double multiplierDamagePassif = 2.;
	
	public static final String nameFirstSpell = "§6Propulsion";
	public static final Integer timerFirstSpell = 15;
	public static final Double powerJumpFirstSpell = 1.8;
	
	public static final String nameSecondSpell = "§6Minage";
	public static final Integer timerSecondSpell = 30;	
	public static final Integer tickSpectralSecondSpell = 100;
	
	public Terrafin(Player player) {
		super(player, Element.TERRE, name);
		resis = 0.9;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.MAROON);
						
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void onSneak() {
		Block blockUnderPlayer = player.getLocation().clone().add(0, -1, 0).getBlock();
		
		if (blockUnderPlayer.getType().equals(Material.AIR))
			player.setVelocity(new Vector(0, -powerJumpFirstSpell, 0));
	}
	
	public void onFall() {
		Double radius = 4.;
		Double y = 0.2;
		
		for (double t = 0; t < 50; t += 0.5) {
			Double x = radius * (float) Math.sin(t);
			Double z = radius * (float) Math.cos(t);
			Location locParticule = player.getLocation().clone().add(x, y, z);
			player.getWorld().spawnParticle(Particle.SMOKE_LARGE, locParticule, 0, 0., 0., 0.);
		}
		
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), 3.5, 1.5, 3.5)) {
			Player playerHit = skylanderHit.getPlayer();
			
			playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
			playerHit.sendMessage(Constants.prefixMessage + "Vous venez de vous faire §3écraser§f par §3"+ player.getName() +"§f.");
			playerHit.damage(player.getFallDistance() * multiplierDamagePassif, player);
		}
	}	
	
	public void firstSpell_Jump() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			player.setVelocity(new Vector(0, powerJumpFirstSpell, 0));
			
			addStatus(null, Status.ONEFALL);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Spectral() {
		if (checkCooldown(nameSecondSpell, true)) {
			Location oldLocation = player.getLocation();
			
			player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
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
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§6" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, vous infligez des §edégats§f lorsque vous §etombez au sol§f. §7(hauteur de la chute * "+ multiplierDamagePassif +")");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous êtes §epropulsé§f dans les airs, les dégats de votre chute sont §eannulé§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous devenez §eintangible§f pendant §e" + SkylanderConverter.convertTicks(tickSpectralSecondSpell) + " secondes§f, vous permettant de traverser les murs. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§6"+ name +"§f est un Skylander §cmélée§f ayant la");
		lore.add("§fcapacité d'infliger des dégats suivant sa chute.");
		
		ItemStack item = new ItemStack(Material.ANVIL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous êtes §epropulsé§f dans les airs,", "§fvos dégats de chute sont annulé.");
		
		ItemStack item = new ItemStack(Material.PISTON, 1);
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
		List<String> lore = Arrays.asList("§fVous devenez §eintangible§f vous permettant de", "§ftraverser les murs pendant §e"+ SkylanderConverter.convertTicks(tickSpectralSecondSpell) +"s§f.");
		
		ItemStack item = new ItemStack(Material.COARSE_DIRT, 1);
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
		meta.setDisplayName("§6Poing de Fer");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
