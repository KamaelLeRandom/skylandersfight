package fr.kamael.skylandersfight.skylanders.terre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
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
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class PrismBreak extends Skylander {
	public static final String name = "PrismBreak";
	
	public static final String nameWeapon = "§6Gemme";
	
	public static final String namePassif = "§6Solidification";
	public static final Integer timerPassif = 30;
	public static final Integer levelPassif = 4;
	
	public static final String nameFirstSpell = "§6Rayon";
	public static final Double damageFirstSpell = 2.;
	public static final Integer tickDelayFirstSpell = 15; 
	public static final Integer timerFirstSpell = 15;
	
	public static final String nameSecondSpell = "§6Cristallisation";
	public static final Double bonusResisSecondSpell = 0.2;
	public static final Integer initialDurationSecondSpell = 15;
	public static final Integer maxDurationSpell = 15;
	public static final Integer timerSecondSpell = 30;
		
	public Integer timeNoFight = 0;
	public Boolean firstSpellActived = false;
	public Boolean secondSpellActived = false;
	public Integer counterDiamond = initialDurationSecondSpell; 
	
	public PrismBreak(Player player) {
		super(player, Element.TERRE, name);
		resis = 0.9;
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
		
	public Boolean onHitBow(Skylander skylanderDamager) { 
		timeNoFight = 0;
		return false;
	}
	
	public Boolean onHitSword(Skylander skylanderDamager) {
		timeNoFight = 0;
		return false; 
	}
		
	public Boolean onDamageSword(Skylander skylanderHit) {
		if (secondSpellActived == false && counterDiamond < maxDurationSpell) {
			counterDiamond++;
			player.setLevel(counterDiamond);
		}
		timeNoFight = 0;
		return false; 
	} 
	
	public void onStart() { 
		player.setLevel(counterDiamond);
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 99999, levelPassif, false, false));
		passif();
		return; 
	}
	
	public void passif() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				timeNoFight++;
				
				if (timeNoFight == timerPassif) {
					timeNoFight = 0;
					player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez de gagner 10 coeurs d'absortion grâce à votre "+ namePassif +"§f.");
					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 99999, levelPassif, false, false));
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void firstSpell_Rayon() {
		if (firstSpellActived) {
			player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de §cdésactiver§f votre " + nameFirstSpell + "§f.");
			firstSpellActived = false;
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
		
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'§aactiver§f votre " + nameSecondSpell + "§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200, false, false));
			firstSpellActived = true;
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for (int i = 2; i <= 20; i++) {
						Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(i));
						
						if (player.getWorld().getBlockAt(loc).getType() != Material.AIR)
							break;
						
						player.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 1, 0., 0., 0., 1, new Particle.DustOptions(Color.AQUA, 1));
						
						for (Entity entity : player.getWorld().getNearbyEntities(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(i)), 1, 1, 1)) {
							if (entity instanceof Player && entity != player) {
								Player playerTarget = (Player) entity;
								Skylander skylanderTarget = plugin.game.getPlayer(playerTarget).getSkylander();
								
								if (skylanderTarget.isAlive() && !mates.contains(skylanderTarget)) {
									playerTarget.damage(damageFirstSpell);	
									player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
								}
							}
						}	
					}
					
					if (!player.getInventory().getItemInMainHand().equals(getItemFirstSpell())) {
						firstSpellActived = false;
					}
					
					if (firstSpellActived == false) {
						player.removePotionEffect(PotionEffectType.SLOW);
						player.removePotionEffect(PotionEffectType.JUMP);
						cancel();
						return;
					}
				}
			}.runTaskTimer(plugin, 0, tickDelayFirstSpell);
		}
	}
	
	public void secondSpell_PowerUp() {
		if (counterDiamond == 0) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous n'avez plus de §ecompteurs§f diamants, vous avez §cperdu§f votre compétence "+ nameSecondSpell +"§f.");
			return;
		}
		
		if (secondSpellActived) {
			ItemManager.giveColorArmor(player, Color.MAROON);
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de §cdésactiver§f votre " + nameSecondSpell + "§f, il vous reste encore §e" + counterDiamond + " compteurs§f diamants.");
			secondSpellActived = false;
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
		
		if (checkCooldown(nameSecondSpell, true)) {
			ItemManager.giveDiamondArmor(player);
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'§aactiver§f votre " + nameSecondSpell + "§f, il vous reste §e" + counterDiamond + " compteurs§f diamants pour le moment.");
			secondSpellActived = true;
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if (secondSpellActived == false || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					if (counterDiamond == 0) {
						ItemManager.giveColorArmor(player, Color.MAROON);
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Vous n'avez plus de §ecompteurs§f diamants, vous avez §cperdu§f votre compétence "+ nameSecondSpell +"§f.");
						cancel();
						return;
					}
					
					counterDiamond--;
					player.setLevel(counterDiamond);
				}
			}.runTaskTimer(plugin, 0, 20);
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§6" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, toutes les §e"+ timerPassif +" secondes sans combattre§f vous gagnez §c10 ❤️§f d'Absortion.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous tirez une rayon laser qui inflige §e"+ damageFirstSpell +" dégats§f toutes les §e"+ SkylanderConverter.convertTicks(tickDelayFirstSpell) +" secondes§f, attention lorsque vous effectuez votre rayon vous êtes §cimmobilisé§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, pour activer cette compétence vous devez avoir au moins 1 §ecompteurs diamants§f, lorsque cette compétence est activé vous obtenez une armure en diamant et chaque vous octroie une seconde de temps. Pour récupérer des compteurs vous devez frapper un joueur. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§6"+ name +"§f est un Skylander §cmélée§f ayant de");
		lore.add("§ftrès grande capacité de Résistance.");
		
		ItemStack item = new ItemStack(Material.DIAMOND, 1);
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
		List<String> lore = Arrays.asList("§fVous tirez un §erayon laser§f,", "§fcelui-ci inflige §e"+ damageFirstSpell +" dégats§f toutes les "+ SkylanderConverter.convertTicks(tickDelayFirstSpell) +"s aux joueurs sur la trajectoire.");
		
		ItemStack item = new ItemStack(Material.DIAMOND, 1);
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
		List<String> lore = Arrays.asList("§fVous pouvez activer cette compétence, lorsqu'elle", "§fest activé, vous gagnez "+ bonusResisSecondSpell*100 +"% de Résistance tant vous avez des Diamants.", "§fCependant vous perdez un Diamant à chaque coup réçu."); 
		
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK, 1);
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
		meta.setDisplayName("§6" + nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
