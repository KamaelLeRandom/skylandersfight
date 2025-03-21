package fr.kamael.skylandersfight.skylanders.feu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
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

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Sunburn extends Skylander {
	public static final String name = "Sunburn";
	
	public static final String nameWeapon = "§4Flamme";
	
	public static final String namePassif = "§4Renaissance";
	public static final Double statsBonusPassif = 0.1;
	
	public static final String nameFirstSpell = "§4Souffle";
	public static final Integer timerFirstSpell = 20;
	public static final Integer rangeFirstSpell = 10;
	public static final Double rangeDetectFirstSpell = 1.5;
	public static final Double damageFirstSpell = 10.;
	
	public static final String nameSecondSpell = "§4Retour";
	public static final Integer timerSecondSpell = 20;
	public static final Double rangeFireSecondSpell = 4.5;
	
	private Boolean haveRez = true;
	private Integer nbStackPassif = 0;
	private Location locationRetour = null;
	
	public Sunburn(Player player) {
		super(player, Element.FEU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.RED);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Boolean onDeath(Skylander skylanderKill) { 
		if (haveRez) {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(plugin.game.getConfig().getNbLifebar() * 10);
			player.setHealth(plugin.game.getConfig().getNbLifebar() * 10);
			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
			player.teleport(plugin.game.getRound().getArena().getRandomPlayerSpawn());
			force += nbStackPassif * statsBonusPassif;
			resis -= nbStackPassif * statsBonusPassif;
			haveRez = false;
			return true;
		}
		
		return false; 
	}
	
	public void firstSpell_Fire() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				rangeFirstSpell, 
				rangeDetectFirstSpell, 
				(location) -> {
				    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 50, 0), 1.5f);
				    location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, dust);

				    for (int i = 0; i < 5; i++) {
				        double offsetX = (Math.random() - 0.5) * 0.8;
				        double offsetY = Math.random() * 1.5;
				        double offsetZ = (Math.random() - 0.5) * 0.8;

				        location.getWorld().spawnParticle(Particle.FLAME, location.clone().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0.01);
				    }
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez touché personne avec votre "+ nameFirstSpell +"§f.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage+ "Vous venez d'être toucher par la compétence "+ nameFirstSpell +"§f de §d"+ player.getName() +"§f.");
				playerTarget.damage(damageFirstSpell, player);
				playerTarget.setFireTicks(10 * 20);
				
				player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §d"+ playerTarget.getName() +"§f.");
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Teleportation() {
		if (player.isSneaking()) {
			player.playSound(player.getLocation(), Sound.BLOCK_CAMPFIRE_CRACKLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de définir la position de votre compétence " + nameSecondSpell + "§f.");
			locationRetour = player.getLocation().clone();
			return;
		}
		
		if (checkCooldown(nameSecondSpell, true)) {
			for (Skylander skylanderAround : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeFireSecondSpell, 2., rangeFireSecondSpell)) {
				Player playerAround = skylanderAround.getPlayer();
				playerAround.playSound(playerAround.getLocation(), Sound.BLOCK_CAMPFIRE_CRACKLE, 1, 1);
				playerAround.sendMessage(Constants.prefixMessage + "Vous avez été touché par la compétence " + nameSecondSpell + "§f de §d" + player.getName() + "§f.");
				playerAround.setFireTicks(10 * 20);
			}
			
			ParticleUtils.teleportationParticule(player.getLocation().clone());
			player.playSound(player.getLocation(), Sound.BLOCK_CAMPFIRE_CRACKLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f, vous êtes revenu à notre position défini.");
			player.teleport(locationRetour);
			ParticleUtils.teleportationParticule(player.getLocation().clone());
			
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
		player.sendMessage("≫ §6" + namePassif + "§f, lorsque vous allez mourir pour la première fois, vous êtes §drésuscité§f à un §dpoint d'apparition aléatoire§f.");
		player.sendMessage("   Vous gagnez §a" + (statsBonusPassif * 100) + "%§f de §dForce§f et de §dRésistance§f par §eCendre§f récupérée.");
		player.sendMessage("   Vous pouvez récupérer des §eCendres§f en §dconsumant§f les corps des §djoueurs éliminés§f.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous envoyez un rayon de feu dans la direction visé, le joueur sur la trajectoire est brulé et subit "+ damageFirstSpell +" dégats. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous êtes retéléporté au point que vous avez défini, avant d'être téléporté vous enflammez tout les joueurs proche ("+ rangeFireSecondSpell +" blocs). Pour définir votre point de téléportation vous devez intéragir avec cette compétence en étant accroupi. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§4"+ name +"§f est un Skylander §cmélée§f possèdant");
		lore.add("§fla capacité unique d'avoir deux vie.");
		
		ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Element.FEU.getColor() + name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous souffez un rayon de feu qui brûle et inflige des dégats au joueur sur la trajectoire.");
		
		ItemStack item = new ItemStack(Material.BLAZE_POWDER);
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
		List<String> lore = Arrays.asList("§fVous êtes retéléporté à votre point défini.", "Pour définir un point vous devez être accroupi.");
		
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
}
