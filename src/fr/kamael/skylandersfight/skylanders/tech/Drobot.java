package fr.kamael.skylandersfight.skylanders.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.utils.DrobotDroid;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Drobot extends Skylander {
	public static final String name = "Drobot";
	
	public static final String nameWeapon = "§eRayon Laser";
	public static final String namePassif = "§eRecyclage";
	public static final Integer timerPassif = 30;
	
	public static final String nameFirstSpell = "§eDécapeur Laser";
	public static final Integer distanceFirstSpell = 20;
	public static final Double rangeFirstSpell = 1.5;
	public static final Double damageFirstSpell = 5.;
	public static final Double nerfResisFirstSpell = 0.05;
	public static final Integer timerFirstSpell = 20;
	
	public static final String nameSecondSpell = "§eProtocole Aérien";
	public static final Integer timerSecondSpell = 20;
	
	public Drobot(Player player) {
		super(player, Element.TECH, name);
		this.force = 1.05;
		this.resis = 0.95;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.YELLOW);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void passif(GamePlayer gamePlayer) {
		if (checkCooldown(namePassif, true)) {
			Player playerRez = gamePlayer.getPlayer();
			playerRez.teleport(player.getLocation());
			playerRez.setGameMode(GameMode.ADVENTURE);

			DrobotDroid droid = new DrobotDroid(playerRez);
			droid.giveEquipement();
			droid.summonInfoArmorStand();
			droid.removeAllMates();
			droid.addMates(gamePlayer.getInitialTeam().getPlayers());

			gamePlayer.setActualTeam(plugin.game.getPlayer(player).getActualTeam());
			gamePlayer.setSkylander(droid);
			
			addCooldown(namePassif, timerPassif);
		}
	}
	
	public void firstSpell_Laser() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				distanceFirstSpell, 
				rangeFirstSpell, 
				(location) -> {
			        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1);

			        location.getWorld().spawnParticle(
			            Particle.REDSTONE,
			            location,
			            1, 
			            0.05, 0.05, 0.05,
			            0,
			            dustOptions
			        );
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Venez n'avez touché personne avec votre compétence " + nameFirstSpell + "§f.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Venez avez été touché par la compétence " + nameFirstSpell + "§f de §6" + player.getName() + "§f.");	
				playerTarget.damage(damageFirstSpell, player);
				skylanderTarget.updateResis(nerfResisFirstSpell);
				
				player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Venez avez touché §6" + playerTarget.getName() + "§f avec votre compétence " + nameFirstSpell + "§f.");				
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Fly() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			
			SpellUtils.fly(
				this, 
				null, 
				null, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
					world.spawnParticle(Particle.SMOKE_NORMAL, location, 15, 0.3, 0.3, 0.3);
				}
			);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶" + element.getColor() + name + "§f◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, vous pouvez recycler les §6joueurs éliminés§f afin de les faire revenir dans la manche en tant que vos §6alliés§f. §b("+ timerPassif +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous tirez un rayon laser qui inflige §6" + damageFirstSpell + " dégats§f et retire §6" + nerfResisFirstSpell*100 + "% de Résistance§f au premier touché. §b("+ timerFirstSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous êtes §6propulsé§f dans les airs avec des Elytra. §b("+ timerSecondSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§e"+ name +"§f est un Skylander §cdistance§f capable de recycler");
		lore.add("§fles joueurs éliminés pour en faire des alliés.");
		ItemStack item = new ItemStack(Material.COMPARATOR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous tirez un rayon laser qui inflige §6"+ damageFirstSpell + " dégats§f",
			"§fet retire §6" + nerfResisFirstSpell*100 + "% de Résistance§f au premier joueur touché."
		);
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
		List<String> lore = Arrays.asList(
			"§fVous êtes §6propulsé§f dans les airs avec des Elytra."
		);
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
