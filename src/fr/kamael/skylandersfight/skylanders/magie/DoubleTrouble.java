package fr.kamael.skylandersfight.skylanders.magie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;

public class DoubleTrouble extends Skylander {
	public static final String name = "Double Trouble";
	
	public static final String nameWeapon = "§5Sceptre";
	
	public static final String namePassif = "§5Laser";
	public static final Integer tickTimerPassif = 15;
	
	public static final String nameFirstSpell = "§5Invocation";
	public static final Integer numberInvocFirstSpell = 3;
	public static final Integer timerFirstSpell = 15;
	
	public static final String nameSecondSpell = "§5Métamorphose";
	public static final Integer durationSecondSpell = 15;
	public static final Integer timerSecondSpell = 30;
	
	private Boolean canUsePassif = true;
	
	public void passif() {
		if (canUsePassif) {
			canUsePassif = false;
			
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				10, 
				0.75, 
			    (location) -> {
			        location.getWorld().spawnParticle(
			            Particle.REDSTONE, 
			            location, 
			            2, 
			            0.05, 0.05, 0.05,
			            new Particle.DustOptions(Color.FUCHSIA, 1.0f)
			        );
			    }
			);
			
			if (skylanderTarget != null) {
				skylanderTarget.getPlayer().damage(6, player);
				player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
			}
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					canUsePassif = true;
					cancel();
					return;
				}
			}.runTaskTimer(plugin, 0, tickTimerPassif);
		}
	}
	
	public void firstSpell_Invocation() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(this, 10, 0.75, null);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur n'a été trouvé.");
				return;
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ nameFirstSpell +" sur §6"+ skylanderTarget.getPlayer().getName() +"§f.");
				
				for (int i = 0; i < numberInvocFirstSpell; i++)
					
				
				addCooldown(nameFirstSpell, timerFirstSpell);
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void secondSpell_Metamorphose() {
		if (checkCooldown(nameSecondSpell, true)) {
			Inventory invMetamorphose = Bukkit.createInventory(player, 18, nameSecondSpell);
			Integer idxInv = 0;
			
			for (GamePlayer gamePlayerEnemy : plugin.game.getPlayers()) 
			{
				if (gamePlayerEnemy.getSkylander().isAlive() && !gamePlayerEnemy.getPlayer().equals(player)) {
					Player playerEnemy = gamePlayerEnemy.getPlayer();
					
					ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
					SkullMeta itM = (SkullMeta) it.getItemMeta();
					itM.setDisplayName("§c"+playerEnemy.getName());
					itM.setOwner(playerEnemy.getName());
					it.setItemMeta(itM);
					invMetamorphose.setItem(idxInv, it);
				}
			}
			player.openInventory(invMetamorphose);
		}
	}
	
	public void secondSpell_Transform(Player playerRecherche) {
		player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez de vous §dtransformez§c en §6"+ player.getName() +"§f, dans "+ durationSecondSpell +" secondes vous reprendrez votre véritable apparence.");
		
		plugin.playerUtils.nickPlayer(player, playerRecherche.getName());
		plugin.playerUtils.changeSkin(player, playerRecherche.getName());
		
		plugin.playerUtils.nickPlayer(playerRecherche, player.getName());
		plugin.playerUtils.changeSkin(playerRecherche, player.getName());
		
		new BukkitRunnable() {
			private Integer timer = durationSecondSpell;
			@Override
			public void run() {
				if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
					plugin.playerUtils.unnickPlayer(player);
					plugin.playerUtils.revertSkin(player);
					
					plugin.playerUtils.unnickPlayer(playerRecherche);
					plugin.playerUtils.revertSkin(playerRecherche);
					
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("≫ " + namePassif + "§f, .");
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
		lore.add("§5"+ name +"§f est un Skylander §cmélée§f capable de troubler");
		lore.add("§fses ennemies avec ces capacités.");
		
		ItemStack item = new ItemStack(Material.GOLDEN_SHOVEL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§5"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§f", "§f");
		
		ItemStack item = new ItemStack(Material.TURTLE_EGG, 1);
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
		List<String> lore = Arrays.asList("§fVous échangez votre apparence avec le joueur choisit", "§fpendant "+ durationSecondSpell +"s.");
		
		ItemStack item = new ItemStack(Material.ARMOR_STAND, 1);
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
