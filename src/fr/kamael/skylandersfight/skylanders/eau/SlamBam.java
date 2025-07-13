package fr.kamael.skylandersfight.skylanders.eau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class SlamBam extends Skylander {
	public static final String name = "Slam Bam";
	
	public static final String namePassif = "§9Poing de Yéti";
	public static final Double damagePassif = 3.;
	
	public static final String nameFirstSpell = "§9Ora-Ora-Ora";
	public static final Double rangeFirstSpell = 4.;
	public static final Integer timerFirstSpell = 20;
	public static final Integer nbHitFirstSpell = 5;

	public static final String nameSecondSpell = "§9Contraction glaciale";
	public static final Integer timerSecondSpell = 20;
	public static final Integer durationSecondSpell = 50;
	public static final Double rangeSecondSpell = 5.;
	public static final Integer durationKnockbackSecondSpell = 8;
	public static final Double valueKnockbackSecondSpell = 0.8;
		
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
		if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR))	
			return damage + damagePassif;
		return damage; 
	}
	
	public Boolean onDamageSword(Skylander skylanderHit) { 
		new BukkitRunnable() {
			@Override
		    public void run() {
				skylanderHit.getPlayer().setNoDamageTicks(skylanderHit.getPlayer().getMaximumNoDamageTicks() / 5);
		    	cancel();
		    	return;
			}
		}.runTaskLater(plugin, 1);
		
		return false; 
	}
	
	public void firstSpell_Force() {
		if (checkCooldown(nameFirstSpell, true)) {			
			ArrayList<Skylander> skylandersHit = SpellUtils.skylanderInFront(this, rangeFirstSpell / 2, rangeFirstSpell, rangeFirstSpell);
			
			if (skylandersHit.size() > 0) {
				player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_HIT, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f, vous avez toucher "+ skylandersHit.size() +" joueurs.");
				
	            new BukkitRunnable() {
	                private int nbHit = nbHitFirstSpell;

	                @Override
	                public void run() {
	                    if (nbHit == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
	                        cancel();
	                        return;
	                    }

	                    Location origin = player.getLocation().clone().add(0, 1, 0);
	                    Vector direction = origin.getDirection().normalize();

	                    for (int i = 1; i <= 3; i++) {
	                        Location punchLocation = origin.clone().add(direction.clone().multiply(i * 0.8));
	                        player.getWorld().spawnParticle(Particle.CRIT, punchLocation, 10, 0.2, 0.2, 0.2, 0.01);
	                        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, punchLocation, 2, 0, 0, 0, 0);
	                    }

	                    for (Skylander skylanderHit : skylandersHit) {
	                        skylanderHit.getPlayer().damage(2.0, player);
	                    }

	                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.7f, 1.2f);
	                    nbHit--;
	                }
	            }.runTaskTimer(plugin, 0, 4);
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f, cependant personne n'était dans la zone d'impact.");
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Resis() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
			player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, false, false));
			player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(valueKnockbackSecondSpell);
			
			ParticleUtils.sphereParticule(plugin, player.getLocation(), Particle.SNOWFLAKE, rangeSecondSpell);
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, rangeSecondSpell, rangeSecondSpell)) {
			    Player playerHit = skylanderHit.getPlayer();
			    playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);
			    playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence " + nameSecondSpell + "§f de §6" + player.getName() + "§f.");
			    playerHit.sendTitle(nameSecondSpell, "§7Vous êtes §3gelé§f pendant " + SkylanderConverter.convertTicks(durationSecondSpell) + "s.", 1, durationSecondSpell, 1);
			    playerHit.getWorld().spawnParticle(Particle.SNOWFLAKE, playerHit.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.01);
			    skylanderHit.addStatus(durationSecondSpell, Status.FREEZE);
			}
			
			new BukkitRunnable() {
				private Integer timer = durationKnockbackSecondSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
						player.sendMessage(Constants.prefixMessage+ "Votre compétence "+ nameSecondSpell +"§f vient de prendre fin, vous retrouvez votre résistance normal.");
						player.removePotionEffect(PotionEffectType.SLOW);
						player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(Constants.baseKnockback);
						cancel();
						return;
					}
				
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ §9" + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, lorsque vous frappez à la main, vous annulez le délai d'invulnérabilité du joueur touché, vous permettant de faire des gros combos.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous enchainez très rapidement "+ nbHitFirstSpell +" coups automatique sur les joueurs qui se trouve devant vous (- de "+ rangeFirstSpell +" blocs). §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous gelez les joueurs autour de vous (- de " + rangeSecondSpell + " blocs) pendant " + SkylanderConverter.convertTicks(durationSecondSpell) + " et vous gagnez " + valueKnockbackSecondSpell*100 + "% de Résistance au recul pendant "+ durationKnockbackSecondSpell +" secondes. §b(" + timerSecondSpell + "s de recharge)");
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
		ItemStack item = new ItemStack(Material.BLUE_ICE, 1);
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
