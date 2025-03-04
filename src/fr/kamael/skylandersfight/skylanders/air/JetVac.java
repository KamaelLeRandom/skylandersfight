package fr.kamael.skylandersfight.skylanders.air;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class JetVac extends Skylander {
	public static final String name = "Jet-Vac";
	
	public static final String namePassif = "§3Bobine d'air";
	public static final Integer windNeedPunchPassif = 50;
	public static final Integer windNeedJumpPassif = 20;

	public static final String nameFirstSpell = "§Rafale de Vent";
	public static final Integer timerFirstSpell = 30;
	public static final Integer windNeedFirstSpell = 40;
	public static final Integer rangeFirstSpell = 10;
	public static final Integer tickImmoFirstSpell = 30;
	public static final Double damageFirstSpell = 4.;
	
	public static final String nameSecondSpell = "§3Tornade";
	public static final Integer timerSecondSpell = 15;
	public static final Integer windNeedSecondSpell = 30;
	public static final Integer rangeSecondSpell = 10;
	public static final Integer tickNauseaSecondSpell = 100;
	public static final Double powerVectorSecondSpell = 1.5;
	
	private ItemStack bow;
	private Integer windLevel;
	
	public JetVac(Player player) {
		super(player, Element.AIR, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.WHITE);
						
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
			
			if (windLevel <= windNeedPunchPassif && bow.containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
				bow.removeEnchantment(Enchantment.ARROW_KNOCKBACK);
				player.updateInventory();
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public void onSneak() { 
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING) || !player.isSneaking() || windLevel >= 100) {
					cancel();
					return;
				}
				
				player.setLevel(++windLevel);
				
				if (windLevel == windNeedPunchPassif) {
					bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
					player.updateInventory();
				}
			}
		}.runTaskTimer(plugin, 0, 2);
		
		return; 
	}
	
	public void passif_DoubleJump() {
		if (updateWindLevel(- windNeedJumpPassif)) {
	        Vector jump = player.getLocation().getDirection().multiply(1.2).setY(1);
	        
	        player.setVelocity(jump);
		} else {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez d'air pour utiliser votre §3Double Saut§f.");
		}
	}
	
	public void firstSpell_Damage() {
		if (checkCooldown(nameFirstSpell, true)) {
			if (updateWindLevel(- windNeedFirstSpell)) {
				Skylander skylanderTarget = SpellUtils.targetPlayer(this, rangeFirstSpell, null);
				
				if (skylanderTarget == null ) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
					return;
				} else {
					Player playerTarget = skylanderTarget.getPlayer();
					skylanderTarget.addStatus(tickImmoFirstSpell, Status.NOMOVE);
					playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage+ "Vous venez d'être toucher par la compétence "+ nameFirstSpell +"§f de §3"+ player.getName() +"§f.");
					playerTarget.sendTitle(nameFirstSpell, "Vous êtes immobilisé pendant "+ SkylanderConverter.convertTicks(tickImmoFirstSpell) +"s.", 1, tickImmoFirstSpell, 1);
					playerTarget.damage(damageFirstSpell, player);
					
					player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §3"+ playerTarget.getName() +"§f.");
					
					addCooldown(nameFirstSpell, timerFirstSpell);
					return;
				}
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
				Skylander skylanderTarget = SpellUtils.targetPlayer(this, rangeSecondSpell, null);
				
				if (skylanderTarget == null ) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
					return;
				} else {
					Player playerTarget = skylanderTarget.getPlayer();
					
					ParticleUtils.tornadoParticule(playerTarget.getLocation(), Particle.SMOKE_NORMAL);
					SpellUtils.tornado(plugin, skylanderTarget);
					
					playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage+ "Vous venez d'être toucher par la compétence "+ nameFirstSpell +"§f de §3"+ player.getName() +"§f.");
					
					player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_BREAK, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f sur §3"+ playerTarget.getName() +"§f.");
					
					addCooldown(nameSecondSpell, timerSecondSpell);
					return;
				}
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
		player.sendMessage("≫ "+ namePassif +"§f, vous avez un §eniveau d'air§f présent dans votre barre d'exp, vous pouvez utiliser §e"+ windNeedJumpPassif +" points d'air§f pour effectuer un §edouble saut§f. En plus, lorsque vous êtes au dessus de §e"+ windNeedPunchPassif +" points d'air§f vous gagnez l'effet §ePunch§f sur votre arc.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous infligez §e"+ damageFirstSpell +" dégats§f et §eimmobilisé§f le joueur ciblé pendant "+ SkylanderConverter.convertTicks(tickImmoFirstSpell) +" secondes, cette compétence demande §e"+ windNeedFirstSpell +" points d'air§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous propulsez en l'air le joueur ciblé, celui-ci obtient un effet Nauséa pendant "+ tickNauseaSecondSpell +", cette compétence demande §e"+ windNeedSecondSpell +" points d'air§f. §b(" + timerSecondSpell + "s de recharge)");
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
		List<String> lore = Arrays.asList("§fVous infligez "+ damageFirstSpell +" dégats et immobilisé le joueur ciblé pendant "+ tickImmoFirstSpell +".");
		
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
		item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
		
		return item;
	}
}
