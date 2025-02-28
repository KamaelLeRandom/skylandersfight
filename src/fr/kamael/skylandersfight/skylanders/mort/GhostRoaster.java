package fr.kamael.skylandersfight.skylanders.mort;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class GhostRoaster extends Skylander {
	public static final String name = "Stealth Elf";
	
	public static final String namePassif = "§7Revanche";
	public static final Integer durationPassif = 10;
	
	public static final String nameFirstSpell = "§7Cape Spectrale";
	public static final Integer timerFirstSpell = 5;
	public static final Double removeHealFirstSpell = 4.;
	public static final Integer ticksInvulFirstSpell = 50;
	
	public static final String nameSecondSpell = "§7Passe-Blocs";
	public static final Integer timerSecondSpell = 5;
	public static final Double removeHealSecondSpell = 6.;
	public static final Integer durationSecondSpell = 5;
	
	public Boolean passifActive = false;
	public Boolean passifAvailable = true;
	
	public GhostRoaster(Player player) {
		super(player, Element.MORT, name);
	}
	
	@Override
	public Boolean onKill(Skylander skylanderDeath) { 
		if (passifActive) {
			
		}
		
		return false; 
	}
	
	@Override
	public Boolean onDeath(Skylander skylanderKill) { 
		return false;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.GREEN);
		
		Inventory inv = player.getInventory();
		// inv.setItem(0, getItemFirstSpell());
		// inv.setItem(1, getItemWeapon());
		// inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void firstSpell_Invul() {
		if (checkCooldown(nameFirstSpell, true)) {
			
			if (SpellUtils.changeLife(this, removeHealFirstSpell)) {
				player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
				
				SpellUtils.invulnerability(plugin, this, ticksInvulFirstSpell);
				
				addCooldown(nameFirstSpell, timerFirstSpell);
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage+ "Vous n'avez plus assez de points de vie pour utiliser "+ nameFirstSpell +"§f.");
			}
		}
	}
}
