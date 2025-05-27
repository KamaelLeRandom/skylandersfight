package fr.kamael.skylandersfight.skylanders.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.entity.SprocketBee;
import fr.kamael.skylandersfight.skylanders.tech.entity.SprocketGolem;
import fr.kamael.skylandersfight.skylanders.tech.entity.SprocketMinecart;
import fr.kamael.skylandersfight.skylanders.tech.entity.SprocketSilverfish;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Sprocket extends Skylander {
	public static final String name = "Sprocket";
	
	public static final String nameWeapon = "§e";
	public static final String namePassif = "§e";
	public static final Double bonusResisPassif = 0.2;
	
	public static final String nameFirstSpell = "§eChar d'assault";
	public static final Integer durationTickFirstSpell = 100;
	public static final Integer timerFirstSpell = 30;
	
	public static final String nameSecondSpell = "§eConstruction";
	public static final Integer timerSecondSpell = 10;
	
	public static final String nameFirstMob = "§eEXP01 - Abeille Empoisonnée";
	public static final Integer timerBuildFirstMob = 5;
	public static final Integer damageFirstMob = 5;
	
	public static final String nameSecondMob = "§eEXP02 - Rat Furtif";
	public static final Integer timerBuildSecondMob = 10;
	public static final Integer damageSecondMob = 4;

	public static final String nameThirdMob = "§eEXP03 - Golem Explosif";
	public static final Integer timerBuildThirdMob = 15;
	public static final Integer damageThirdMob = 6;
	public static final Integer damageExplosionThirdMob = 20;
	
	private Inventory invSecondSpell = getSecondSpellInventory();
	private Boolean secondSpellActive = false;
	private ArrayList<CustomEntity> mobs = new ArrayList<CustomEntity>(); 
	
	public Sprocket(Player player) {
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
	
	public Boolean onHitBow(Skylander skylanderDamager) { 
		if (secondSpellActive) {
			secondSpellActive = false;
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Votre " + nameSecondSpell + "§f a été §cannulé§f car vous avez subi des dégats.");
		}
		return false; 
	}
	
	public Boolean onHitSword(Skylander skylanderDamager) { 
		if (secondSpellActive) {
			secondSpellActive = false;
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Votre " + nameSecondSpell + "§f a été §cannulé§f car vous avez subi des dégats.");
		}
		return false; 
	}
	
	public void onStart() { 
		new BukkitRunnable() {
			private Boolean hasBonus = false;
			
			@Override
			public void run() {
				if (!plugin.game.isState(GameState.FIGHTING) || !alive) {
					cancel();
					return;
				}
				
				mobs.removeIf(e -> e.getEntity() == null);
				
				if (mobs.size() > 1)
					if (!hasBonus)
						resis -= bonusResisPassif;
				else
					if (hasBonus)
						resis += bonusResisPassif;
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void firstSpell_Minecart() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_MINECART_INSIDE, 1, 1);
			player.sendMessage("Vous venez d'utiliser votre compétence " + nameFirstSpell + "§f.");
			new SprocketMinecart(this, this.player.getLocation().clone().add(0, 0.5, 0));
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Inventory() {
		if (secondSpellActive) {
			secondSpellActive = false;
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Votre " + nameSecondSpell + "§f a été §cannulé§f par vous même.");
			return;
		}
		
		if (checkCooldown(nameSecondMob, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
			player.sendMessage("Vous venez d'ouvrir votre menu de " + nameSecondSpell + "§f.");
			player.openInventory(invSecondSpell);
			return;
		}
	}
	
	public void secondSpell_Build(ItemStack it) {
		ItemMeta itMeta = it.getItemMeta();
		
		if (itMeta != null) {
			String itName = itMeta.getDisplayName();
			
			Integer timerBuild = switch (itName) {
			    case nameFirstMob -> timerBuildFirstMob;
			    case nameSecondMob -> timerBuildSecondMob;
			    case nameThirdMob -> timerBuildThirdMob;
			    default -> null;
			};
			
			if (timerBuild == null) return;

			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200, false, false));
				
			secondSpellActive = true;
			
			new BukkitRunnable() {
				private Integer timer = timerBuild;
				@Override
				public void run() {
					if (secondSpellActive == false) {
						player.removePotionEffect(PotionEffectType.SLOW);
						player.removePotionEffect(PotionEffectType.JUMP);
						cancel();
						return;
					}
					
					if (timer == 0) {
						secondSpellActive = false;
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Vous venez de finir la construction de votre " + itName + "§f.");
						player.removePotionEffect(PotionEffectType.SLOW);
						player.removePotionEffect(PotionEffectType.JUMP);
						summonMob(itName);
						addCooldown(nameSecondSpell, timerSecondSpell);
						cancel();
						return;
					}
					player.sendTitle(itName, "Temps restant : " + (timerBuild - timer) + "s.", 1, 20, 1);
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
		}
	}
	
	private void summonMob(String itName) {
	    switch (itName) {
	        case nameFirstMob -> mobs.add(new SprocketBee(this, player.getLocation()));
	        case nameSecondMob -> mobs.add(new SprocketSilverfish(this, player.getLocation()));
	        case nameThirdMob -> mobs.add(new SprocketGolem(this, player.getLocation()));
	    }
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§5"+ name +"§f est un Skylander §cmélée§f capable de construire");
		lore.add("§fdiverses machines pour l'aider.");
		ItemStack item = new ItemStack(Material.LIGHTNING_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemPassif() {
		List<String> lore = Arrays.asList(
			"§f", 
			"§f", 
			"§f"
		);
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(namePassif);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§f",
			"§f",
			"§f"
		);
		ItemStack item = new ItemStack(Material.MINECART, 1);
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
			"§f",
			"§f"
		);
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
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
	
	private static Inventory getSecondSpellInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, nameSecondSpell);
		inv.setItem(1, getBeeEgg());
		inv.setItem(4, getSilverfishEgg());
		inv.setItem(7, getGolemEgg());
		return inv;
	}
	
	public static ItemStack getBeeEgg() {
		List<String> lore = Arrays.asList(
			nameFirstMob + "§f est un groupe de trois abeilles", 
			"§fqui empoisonne le joueur lorsqu'il est attaqué.",
			"§c/!\\§f Temps de construction : §c" + timerBuildFirstMob + " secondes§f."
		);
		ItemStack item = new ItemStack(Material.BEE_SPAWN_EGG, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameFirstMob);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getSilverfishEgg() {
		List<String> lore = Arrays.asList(
			nameSecondMob + "§f est un groupe de cinq rats", 
			"§fqui sont invisible tant qu'ils n'ont pas attaqué.",
			"§c/!\\§f Temps de construction : §c" + timerBuildSecondMob + " secondes§f."
		);
		ItemStack item = new ItemStack(Material.SILVERFISH_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondMob);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getGolemEgg() {
		List<String> lore = Arrays.asList(
			nameThirdMob + "§f est un un golem de vapeur", 
			"§ftrès puissant qui explose à sa mort.",
			"§c/!\\§f Temps de construction : §c" + timerBuildThirdMob + " secondes§f."
		);
		ItemStack item = new ItemStack(Material.HUSK_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameThirdMob);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
