package fr.kamael.skylandersfight.skylanders.air;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Scratch extends Skylander {
	public static final String name = "Scratch";
	
	public static final String namePassif = "§3";
	public static final Integer nbHitPassif = 2;
	public static final Double pourcentSpeedPassif = 0.1;
	
	public static final String nameFirstSpell = "§3Conversion";
	public static final Integer timerFirstSpell = 30;
	public static final Double pourcentForceFirstSpell = 0.01;
	
	public static final String nameSecondSpell = "§3Affalement";
	public static final Integer timerSecondSpell = 30;
	public static final Double rangeSecondSpell = 5.;
	public static final Integer tickRootSecondSpell = 40;
	public static final Double damageSecondSpell = 4.;

	private Integer multiplierSpeed = 0;
	private Integer nbHit = 0;
	
	public Scratch(Player player) {
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
	
	public Boolean onHitBow(Skylander skylanderDamager) { 
		nbHit = 0;
		return false; 
	} 
	
	public Boolean onHitSword(Skylander skylanderDamager) { 
		nbHit = 0;
		return false; 
	}
	
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (++nbHit%nbHitPassif == 0) {
			if (multiplierSpeed < 25) {
				multiplierSpeed++;
				
				player.setLevel(multiplierSpeed);
				
				Double baseSpeed = 0.1; 
				Double newSpeed = baseSpeed * (1 + (pourcentSpeedPassif * multiplierSpeed));
				
		        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		        if (attribute != null)
		            attribute.setBaseValue(newSpeed);
			}
		}
		
		return false; 
	}
	
	public void firstSpell_Buff() {
		if (checkCooldown(nameFirstSpell, true)) {
			if (multiplierSpeed <= 0) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous avez §crien à convertir§f pour le moment.");
				return;
			} else {
				Double bonusForce = multiplierSpeed * pourcentForceFirstSpell;
				
				multiplierSpeed = 0;
				
		        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		        if (attribute != null)
		            attribute.setBaseValue(0.1);
		        
		        force += bonusForce;
				
				player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f, ce qui vous a octroyé un bonus de force de "+ bonusForce*100 +"%.");
				player.setLevel(multiplierSpeed);

				addCooldown(nameFirstSpell, timerFirstSpell);
				return;
			}
		}
	}
	
	public void secondSpell_Root() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				
				skylanderHit.addStatus(tickRootSecondSpell, Status.NOMOVE);
				
				playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
				playerHit.sendTitle(nameSecondSpell, "§7Immobilisé de "+ SkylanderConverter.convertTicks(tickRootSecondSpell) +"s.", 1, tickRootSecondSpell, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être toucher par la compétence "+ nameSecondSpell +"§f de §3"+ player.getName() +"§f.");
				playerHit.damage(damageSecondSpell, player);
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§3" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, lorsque vous enchaînez "+ nbHitPassif +" coups sur un joueur sans prendre de dégats, vous gagnez "+ pourcentSpeedPassif*100 +"% de vitesse de déplacement en plus. (cummulable 25 fois à la fois)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous convertissez votre vitesse bonus en §eForce permanentes§f §7(1 level = "+ pourcentForceFirstSpell*100 +"%)§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous infligez §e"+ damageSecondSpell +" dégats§f et §eimmobilisé§f les joueurs proche ("+ rangeSecondSpell +" blocs). §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3"+ name +"§f est un Skylander §cmélée§f");
		lore.add("§fpouvant atteindre une très grand vitesse.");
		
		ItemStack item = new ItemStack(Material.QUARTZ, 1);
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
		List<String> lore = Arrays.asList("§f.");
		
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
		List<String> lore = Arrays.asList("§f.");
		
		ItemStack item = new ItemStack(Material.ANVIL, 1);
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
		meta.setDisplayName("§3Griffe");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
