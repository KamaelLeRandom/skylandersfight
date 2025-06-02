package fr.kamael.skylandersfight.skylanders.vie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.vie.entity.ZooLouChicken;
import fr.kamael.skylandersfight.skylanders.vie.entity.ZooLouPig;
import fr.kamael.skylandersfight.skylanders.vie.entity.ZooLouWolf;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class ZooLou extends Skylander {
	public static final String name = "Zoo Lou";
	
	public static final String nameWeapon = "§2Sceptre Magique";
	public static final String namePassif = "§2Appel d'Oiseau";
	public static final Integer timerPassif = 2;
	public static final Integer damagePassif = 4;
	
	public static final String nameFirstSpell = "§2Ode aux Loups";
	public static final Integer timerFirstSpell = 15;
	public static final Integer healthWolfFirstSpell = 5;
	public static final Double healFirstSpell = 1.;
	public static final Integer numberOfWolfFirstSpell = 3;
	public static final Double damageWolfFirstSpell = 4.;
	
	public static final String nameSecondSpell = "§2Chant du Sanglier";
	public static final Integer timerSecondSpell = 30;
	public static final Double damagePigSecondSpell = 6.;
	
	private Boolean canUsePassif = true;
	private ArrayList<CustomEntity> listWolf = new ArrayList<CustomEntity>();
	private ZooLouPig pig = null;
	
	public ZooLou(Player player) {
		super(player, Element.VIE, name);
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
	
	public void passif_Chicken() {
		if (canUsePassif) {
			canUsePassif = false;
			
			new ZooLouChicken(this, player.getLocation());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					canUsePassif = true;
					cancel();
					return;
				}
			}.runTaskLater(plugin,timerPassif * 20);
		}
	}
	
	public void firstSpell_Wolf() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameFirstSpell + "§f.");
		
			listWolf.removeIf(w -> w.getEntity() == null);
			for (CustomEntity entity : listWolf) {
				entity.removeEntity();
			}
			
			for (int i = 0; i < numberOfWolfFirstSpell; i++) {
				listWolf.add(new ZooLouWolf(this, player.getLocation()));
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Pig() {
		if (pig != null) {
			pig.jump();
			return;
		}
		
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_PIG_SADDLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
		
			pig = new ZooLouPig(this, player.getLocation());
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void secondSpell_Reset() {
		pig = null;
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ " + namePassif + "§f, en cliquant sur votre " + nameWeapon + "§f, vous invoquez un oiseau qui se déplace tout droit et inflige §a" + damagePassif + " dégats§f aux joueurs touchés.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous invoquez " + numberOfWolfFirstSpell + " loups qui inflige §a" + damageWolfFirstSpell + " dégats§f et vous soigne de §c" + healFirstSpell/2 + "❤️§f à chaque coup. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous invoquez et montez sur un cochon qui saute en l'air et inflige " + damagePigSecondSpell + " dégats aux joueurs proche de la retombée. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§2"+ name +"§f est un Skylander §chybride§f maîtrisant");
		lore.add("§fla puissance des animaux.");
		ItemStack item = new ItemStack(Material.WOLF_SPAWN_EGG, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§2"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§f", 
			"§f"
		);
		ItemStack item = new ItemStack(Material.BONE, 1);
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
		ItemStack item = new ItemStack(Material.CARROT, 1);
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
