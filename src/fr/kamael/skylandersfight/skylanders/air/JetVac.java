package fr.kamael.skylandersfight.skylanders.air;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
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
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class JetVac extends Skylander {
	public static final String name = "Jet-Vac";
	
	public static final String namePassif = "§3Bobine d'air";
	public static final Integer tickRechargeWindPassif = 1;
	public static final Integer windNeedFly = 2;

	public static final String nameFirstSpell = "§3Rafale de Vent";
	public static final Integer timerFirstSpell = 10;
	public static final Integer windNeedFirstSpell = 25;
	public static final Integer rangeFirstSpell = 15;
	public static final Double damageFirstSpell = 4.;
	
	public static final String nameSecondSpell = "§3Tourbillon";
	public static final Integer timerSecondSpell = 10;
	public static final Integer windNeedSecondSpell = 25;
	public static final Integer rangeSecondSpell = 15;
	
	private ItemStack bow;
	private Integer windLevel;
	
	public JetVac(Player player) {
		super(player, Element.AIR, name);
		windLevel = 100;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
						
		player.setLevel(windLevel);
		player.setAllowFlight(true);
		
		bow = getItemWeapon();
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, bow);
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	private Boolean updateWindLevel(Integer value) {
		if (windLevel + value >= 0) {
			windLevel += value;
			player.setLevel(windLevel);
			return true;
		} else {
			return false;
		}
	}
	
	public void onStart() { 
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				if (player.isFlying()) {
					windLevel -= windNeedFly;
					if (windLevel <= 0) {
						windLevel = 0;
						player.setAllowFlight(false);
					}
					player.setLevel(windLevel);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		
		return; 
	}
	
	public void onSneak() { 
		player.setAllowFlight(true);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING) || !player.isSneaking() || windLevel >= 100) {
					cancel();
					return;
				}
				
				if (player.isFlying() == false) {
					player.setLevel(++windLevel);	
				}
			}
		}.runTaskTimer(plugin, 0, tickRechargeWindPassif);
		
		return; 
	}
	
	public void firstSpell_Damage() {
		if (checkCooldown(nameFirstSpell, true)) {
			if (updateWindLevel(- windNeedFirstSpell)) {
				Skylander skylanderTarget = SpellUtils.targetPlayer(this, rangeFirstSpell, 1.5, 
					(location) -> {
				        location.getWorld().spawnParticle(
					        Particle.EXPLOSION_LARGE, 
					        location, 
					        2, 
					        0.05, 0.05, 0.05,
					        0
				        );
					}
				);
				
				if (skylanderTarget == null ) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous n'avez touché personne avec votre "+ nameFirstSpell +"§f.");
				} else {
					Player playerTarget = skylanderTarget.getPlayer();

					playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage+ "Vous venez d'être toucher par la compétence "+ nameFirstSpell +"§f de §3"+ player.getName() +"§f.");
					playerTarget.damage(damageFirstSpell, player);
					playerTarget.setVelocity(player.getLocation().getDirection().clone().add(new Vector(0., 0.2, 0.)).multiply(2.));
					
					player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §3"+ playerTarget.getName() +"§f.");
				}
				
				addCooldown(nameFirstSpell, timerFirstSpell);
				return;
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez d'air pour utiliser " + nameFirstSpell + "§f.");
				return;
			}
		}
	}
	
	public void secondSpell_Tornado() {
		if (checkCooldown(nameSecondSpell, true)) {
			if (updateWindLevel(- windNeedSecondSpell)) {
				Skylander skylanderTarget = SpellUtils.targetPlayer(this, rangeSecondSpell, 1., 
					(location) -> {
						location.getWorld().spawnParticle(
							Particle.EXPLOSION_NORMAL, 
							location, 
						    1, 
						    0., 0., 0.,
						    0
					    );
					}
				);
				
				if (skylanderTarget == null ) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous n'avez touché personne avec votre "+ nameSecondSpell + "§f.");
				} else {
					Player playerTarget = skylanderTarget.getPlayer();
					
					playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage+ "Vous venez d'être toucher par la compétence "+ nameSecondSpell +"§f de §3"+ player.getName() +"§f.");
					
					player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f sur §3"+ playerTarget.getName() +"§f.");
					
					ParticleUtils.tornadoParticule(playerTarget.getLocation(), Particle.EXPLOSION_NORMAL);
					
					new BukkitRunnable() {
						private Integer timer = 100;
						private float yaw = 0;
						private double yOffset = 0;
						 
						@Override
						public void run() {
							if (!plugin.game.isState(GameState.FIGHTING) || !alive || timer == 0) {
								cancel();
								return;
							}
							
							yOffset += 0.003;
				            yaw += 10;
				            if (yaw >= 360) yaw -= 360;
				            
				            Location loc = playerTarget.getLocation();
				            loc.setYaw(yaw);
				            loc.setY(loc.getY() + yOffset);
				            playerTarget.teleport(loc);
							
							timer--;
						}
					}.runTaskTimer(plugin, 0, 1);
				}
				
				addCooldown(nameSecondSpell, timerSecondSpell);
				return;
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez d'air pour utiliser " + nameSecondSpell + "§f.");
				return;
			}
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§3" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, vous avez un §eniveau d'air§f présent dans votre barre d'exp, vous pouvez voler en consummant votre air.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous §eéjectez en arrière§f le joueur sur la trajectoire de votre regard, de plus le joueur touché subit §e"+ damageFirstSpell +" dégats§f, cette compétence demande §e"+ windNeedFirstSpell +" points d'air§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous §eenvoyez en l'air§f le joueur sur la trajection de votre regard, cette compétence demande §e"+ windNeedSecondSpell +" points d'air§f. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3"+ name +"§f est un Skylander à §cdistance§f");
		lore.add("§ftrès mobile mais doit gérer sa ressource d'énergie");
		
		ItemStack item = new ItemStack(Material.STRING, 1);
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
		List<String> lore = Arrays.asList("§fVous infligez "+ damageFirstSpell +" dégats et renvoyer en arrière le joueur ciblé.");
		
		ItemStack item = new ItemStack(Material.WHITE_DYE, 1);
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
		List<String> lore = Arrays.asList("§fVous envoyez en l'air le joueur ciblé.");
		
		ItemStack item = new ItemStack(Material.STRING, 1);
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
		meta.setDisplayName("§3Pistolet à air");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		
		return item;
	}
}
