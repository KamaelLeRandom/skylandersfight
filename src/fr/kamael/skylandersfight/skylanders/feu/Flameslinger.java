package fr.kamael.skylandersfight.skylanders.feu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Flameslinger extends Skylander {
	public static final String name = "Flameslinger";
	
	public static final String nameWeapon = "§4Arc'flamme";
	public static final String namePassif = "§4Sniper";
	public static final Double minRangePassif = 10.;
	
	public static final String nameFirstSpell = "§4";
	public static final Integer timerFirstSpell = 30;
	public static final Integer secDurationFirstSpell = 10;
	
	public static final String nameSecondSpell = "§4Course Enflammée";
	public static final Integer tickDurationSecondSpell = 200;
	public static final Integer timerSecondSpell = 30;

	private Boolean firstSpellActived = false;
	
	public Flameslinger(Player player) {
		super(player, Element.FEU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}

	@Override
	public Double addDamage(Double damage, Skylander skylanderHit) { 
		Double distance = player.getLocation().distance(skylanderHit.getPlayer().getLocation());
		
		if (distance >= minRangePassif) {
			Double pourcent = 1 + ((distance - minRangePassif) / 100);
			
			return damage * pourcent;
		}
		
		return damage; 
	}
	
	@Override
	public void onShoot(Projectile projectile) { 
		if (firstSpellActived) {
			// Mettres les flammes.
		}
		
		return; 
	}
	
	public void firstSpell_FireArrow() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_CAMPFIRE_CRACKLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utliser votre compétence " + nameFirstSpell + "§f.");
			firstSpellActived = true;
			
			new BukkitRunnable() {
				private Integer timer = secDurationFirstSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Votre compétence " + nameFirstSpell + "§f vient de prendre fin.");
						firstSpellActived = false;
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Run() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utliser votre compétence " + nameSecondSpell + "§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
			
			new BukkitRunnable() {
				private Integer timer = tickDurationSecondSpell;
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.removePotionEffect(PotionEffectType.SPEED);
						cancel();
						return;
					}
					
					// Mettre les flammes.
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 1);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("≫ §6" + namePassif + "§f, .");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, . §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
 	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§4"+ name +"§f est un Skylander §cdistance§f possèdant");
		lore.add("§fdes flèches enflammées.");
		ItemStack item = new ItemStack(Material.BOW, 1);
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
		List<String> lore = Arrays.asList(
			"§f."
		);
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
		List<String> lore = Arrays.asList(
			"§f."
		);
		ItemStack item = new ItemStack(Material.RABBIT_FOOT, 1);
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
