package fr.kamael.skylandersfight.skylanders.air;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class LightningRod extends Skylander {
	public static final String name = "Lightning Rod";
	
	public static final String namePassif = "§3Onde Électrique";
	public static final Double rangePassif = 0.8;
	public static final Double damagePassif = 1.;
	
	public static final String nameFirstSpell = "§3Foudre";
	public static final Integer timerFirstSpell = 30;
	public static final Double rangeFirstSpell = 5.;
	public static final Integer tickStunFirstSpell = 40;
	
	public static final String nameSecondSpell = "§3Lévitation";
	public static final Integer timerSecondSpell = 30;
	public static final Integer durationSecondSpell = 5;
	
	public LightningRod(Player player) {
		super(player, Element.AIR, name);
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
	
	public void onShoot(Projectile projectile) {
		if (projectile instanceof Arrow) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					if (projectile.isOnGround()) {
						passif(projectile);
						cancel();
						return;
					}
				}
			}.runTaskTimer(plugin, 0, 5);
		}
		
		return; 
	}
	
	public void passif(Projectile projectile) {
		ParticleUtils.sphereParticule(plugin, projectile.getLocation().clone().subtract(0, 1, 0), Particle.ELECTRIC_SPARK, rangePassif);
		
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, projectile.getLocation(), rangePassif, rangePassif, rangePassif)) {
			skylanderHit.getPlayer().damage(damagePassif, projectile);
		}
	}
	
	public void firstSpell_Stun() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			player.getWorld().strikeLightningEffect(player.getLocation());
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeFirstSpell, 2., rangeFirstSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				
				playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
				playerHit.sendTitle(nameFirstSpell, "§7Étourdissement de "+ SkylanderConverter.convertTicks(tickStunFirstSpell) +"s.", 2, 36, 2);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez de subir la compétence "+ nameFirstSpell +"§f de §3"+ playerHit.getName() + "§f.");
				playerHit.getWorld().strikeLightningEffect(playerHit.getLocation());
				
				skylanderHit.addStatus(tickStunFirstSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Fly() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			player.setAllowFlight(true);
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpell * 2;
				
				@Override
				public void run() {
				    Location footLocation = player.getLocation().clone().subtract(0, 0.1, 0);
				    player.getWorld().spawnParticle(
				        Particle.CLOUD,
				        footLocation,
				        15,
				        0.3, 0.05, 0.3,
				        0.01
				    );
					
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Votre compétence "+ nameFirstSpell +"§f vient de prendre fin.");
						player.setFlying(false);
						player.setAllowFlight(false);
						cancel();
						return;
					}
				
					timer--;
				}
			}.runTaskTimer(plugin, 0, 10);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§3" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, lorsqu'une §eflèche atterit proche d'une joueur§f (moins de "+ rangePassif + " bloc) celui-ci subit "+ damagePassif + " dégats.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous étourdissez tout les joueurs proche §7(dans un rayon de "+ rangeFirstSpell +" blocs)§f pendant "+ SkylanderConverter.convertTicks(tickStunFirstSpell) +". §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous pouvez §evoler§f dans les airs pendant "+ durationSecondSpell +" secondes. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3"+ name +"§f est un Skylander à §cdistance§f");
		lore.add("§fpouvant étourdir, voler et bien d'autre.");
		
		ItemStack item = new ItemStack(Material.LIGHTNING_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous étourdissez tout les joueurs proche de vous.");
		
		ItemStack item = new ItemStack(Material.YELLOW_DYE, 1);
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
		List<String> lore = Arrays.asList("§fVous pouvez §3voler§f dans les airs pendant §3"+ durationSecondSpell +" §fsecondes.");
		
		ItemStack item = new ItemStack(Material.FEATHER, 1);
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
		ItemStack item = new ItemStack(Material.BOW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3Éclair");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		
		return item;
	}
}
