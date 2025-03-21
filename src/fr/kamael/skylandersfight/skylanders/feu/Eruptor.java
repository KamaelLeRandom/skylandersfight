package fr.kamael.skylandersfight.skylanders.feu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import fr.kamael.skylandersfight.skylanders.feu.entity.EruptorFireball;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Eruptor  extends Skylander {
	public static final String name = "Eruptor";
	
	public static final String namePassif = "§4Solidification";
	public static final Integer durationPassif = 8;
	public static final Double pourcentResisPassif = 0.10;
	
	public static final String nameFirstSpell = "§4Sphère de lave";
	public static final Integer timerFirstSpell = 20;
	public static final Double damageFirstSpell = 5.;
	
	public static final String nameSecondSpell = "§4Eruption";
	public static final Integer timerSecondSpell = 30;
	public static final Integer durationSecondSpell = 3;
	public static final Integer rangeSecondSpell = 3;
	
	public Eruptor(Player player) {
		super(player, Element.FEU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.RED);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell(3));
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void passifSpell() {
		resis -= pourcentResisPassif;
		
		new BukkitRunnable() {
			private Integer timer = durationPassif;
			
			@Override
			public void run() {
				if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
					resis += pourcentResisPassif;
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void firstSpell_Fireball() {
		ItemManager.removeAmount(player.getEquipment().getItemInMainHand(), -1);
		
		player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez de lancer votre "+ nameFirstSpell +"§f.");
			
		new EruptorFireball(this, player.getLocation());
			
		passifSpell();
		
		new BukkitRunnable() {
			private Integer timer = timerFirstSpell;
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				if (timer == 0) {
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
					player.sendMessage(Constants.prefixMessage+ "Vous venez de récupérer une charge de "+ nameFirstSpell + "§f.");
					player.getInventory().addItem(getItemFirstSpell(1));
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void secondSpell_Lava() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			
			ArrayList<BlockState> listBlocks = new ArrayList<BlockState>();
			
			Integer xCoord = player.getLocation().getBlockX();
			Integer yCoord = player.getLocation().getBlockY();
			Integer zCoord = player.getLocation().getBlockZ();
			
			for (int x = -rangeSecondSpell; x <= rangeSecondSpell; x++) {
				for (int z = -rangeSecondSpell; z <= rangeSecondSpell; z++) {
					Block blockToReplace = player.getWorld().getBlockAt(xCoord + x, yCoord, zCoord + z);
					listBlocks.add(blockToReplace.getState());
					blockToReplace.setType(Material.LAVA);
				}
			}
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						for (BlockState block : listBlocks) {
							block.update(true, false);
						}
						
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			passifSpell();
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶" + element.getColor() + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ " + namePassif +"§f, .");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, . §b(" + Spyro.timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, . §b(" + Spyro.timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§4"+ name +"§f est un Skylander §cmélée§f très résistant");
		lore.add("§fet maîtrisant des techniques de lave.");
		
		ItemStack item = new ItemStack(Material.MAGMA_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell(Integer number) {
		List<String> lore = Arrays.asList("§fVous lancez une boule de feu droit devant vous,", "qui explose quand elle touche le sol infligeant "+ damageFirstSpell +" dégats§f aux joueurs proche.");
		
		ItemStack item = new ItemStack(Material.FIRE_CHARGE, number);
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
		List<String> lore = Arrays.asList("§fVous entrez en éruption créant une zone", "§fde §4lave§f autour de vous pendant §4"+ durationSecondSpell +"s§f.");
		
		ItemStack item = new ItemStack(Material.LAVA_BUCKET, 1);
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
		meta.setDisplayName("§2Lave");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
