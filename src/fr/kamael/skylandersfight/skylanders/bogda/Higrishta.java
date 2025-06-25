package fr.kamael.skylandersfight.skylanders.bogda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.bogda.entity.HigrishtaSphere;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Higrishta extends Skylander {
	public static final String name = "Higrishta";
	
	public static final String nameWeapon = "§d";
	public static final String namePassif = "§d";
	public static final Double bonusPassif = 0.2;
	
	public static final String nameFirstSpell = "§dSphère Anti-matière";
	public static final Integer timerFirstSpell = 45;
	public static final Integer numberOfFirst = 3;
	public static final Double damageFirstSpell = 6.;
	
	public static final String nameSecondSpell = "§dTrou Noir";
	public static final Integer timerSecondSpell = 30;
	public static final Double distanceSecondSpell = 25.;
	public static final Integer durationSecondSpell = 10;
	
	public Higrishta(Player player) {
		super(player, Element.BOGDA, name);
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
	
	public void firstSpell_Sphere() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell + "§f.");
			
			for (int i = 0; i < numberOfFirst; i++)
				new HigrishtaSphere(this, player.getLocation());
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Blackhole() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell + "§f.");
			
			List<Skylander> skylandersEnemy = plugin.game.getPlayers().stream()
                    .map(GamePlayer::getSkylander)
                    .filter(s -> s.isAlive() && !mates.contains(s))
                    .collect(Collectors.toList());
			
	        Location blackholeLocation = player.getLocation().clone().add(0, 2, 0);
	        World world = player.getWorld();
	        
	        new BukkitRunnable() {
	            private Integer timer = durationSecondSpell * 10;

	            @Override
	            public void run() {
	                if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
	                	player.playSound(player.getEyeLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
	                	player.sendMessage(Constants.prefixMessage + "Votre compétence "+ nameSecondSpell + "§f vient de prendre fin.");
	                    cancel();
	                    return;
	                }

	                world.spawnParticle(Particle.PORTAL, blackholeLocation, 50, 0.5, 0.5, 0.5, 0.1);
	                
	                for (Skylander skylanderEnemy : skylandersEnemy) {
	                	Player playerEnemy = skylanderEnemy.getPlayer();
	                    Double distance = playerEnemy.getLocation().distance(blackholeLocation);
	                    
	                    if (distance > distanceSecondSpell) {
	                    	Double strength = 1.5 * (1 - (distance / distanceSecondSpell)); 
		                    Vector direction = blackholeLocation.toVector().subtract(playerEnemy.getLocation().toVector());
		                    Vector pull = direction.normalize().multiply(strength);

		                    playerEnemy.setVelocity(playerEnemy.getVelocity().add(pull));	
	                    }
	                }

	                timer--;
	            }
	        }.runTaskTimer(plugin, 0, 2); 
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
		player.sendMessage("   §f▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + namePassif + "§f, .");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameFirstSpell + "§f, . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameSecondSpell + "§f, . §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander §cmélée§f ...");
		ItemStack item = new ItemStack(Material.CRYING_OBSIDIAN, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d"+name);
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
		ItemStack item = new ItemStack(Material.GRAY_DYE, 1);
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
		ItemStack item = new ItemStack(Material.CRYING_OBSIDIAN, 1);
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
