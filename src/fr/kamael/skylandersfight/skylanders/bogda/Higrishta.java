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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
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
			
			Location center = player.getLocation();
			Vector direction = center.getDirection().normalize();

			Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();

			Location rightLocation = center.clone().add(right.multiply(1));
			Location leftLocation = center.clone().subtract(right.multiply(1));

			new HigrishtaSphere(this, rightLocation);
			new HigrishtaSphere(this, leftLocation);		
			
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
	        
			ArmorStand armorStand = world.spawn(blackholeLocation.clone(), ArmorStand.class);
			armorStand.setInvisible(true);
			armorStand.setGravity(false);
			armorStand.setInvulnerable(true);
			armorStand.setMarker(true);
			armorStand.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
	        
	        new BukkitRunnable() {
	            private Integer timer = durationSecondSpell * 10;
	            private Double yawAngle = 0.;
	            private Double pitchAngle = 0.;
	            private Double rollAngle = 0.;

	            @Override
	            public void run() {
	                if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
	                	player.playSound(player.getEyeLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
	                	player.sendMessage(Constants.prefixMessage + "Votre compétence "+ nameSecondSpell + "§f vient de prendre fin.");
	                	armorStand.remove();
	                	cancel();
	                    return;
	                }

	                world.spawnParticle(Particle.PORTAL, blackholeLocation, 50, 0.5, 0.5, 0.5, 0.1);
	                
	                yawAngle += Math.toRadians(10);   
	                pitchAngle += Math.toRadians(4);
	                rollAngle += Math.toRadians(6);

	                if (yawAngle > Math.PI * 2) yawAngle -= Math.PI * 2;
	                if (pitchAngle > Math.PI * 2) pitchAngle -= Math.PI * 2;
	                if (rollAngle > Math.PI * 2) rollAngle -= Math.PI * 2;

	                armorStand.setHeadPose(new EulerAngle(pitchAngle, yawAngle, rollAngle));
	                
	                for (Skylander skylanderEnemy : skylandersEnemy) {
	                	Player playerEnemy = skylanderEnemy.getPlayer();
	                    Double distance = playerEnemy.getLocation().distance(blackholeLocation);
	                    
	                    if (distance < distanceSecondSpell) {
	                    	if (distance < 0.8) {
	                    	    playerEnemy.setVelocity(new Vector(0, 0, 0));
	                    	    continue;
	                    	} else {
		                    	Double strength = 0.3 * (1 - (distance / distanceSecondSpell)); 
		                    	Vector direction = blackholeLocation.toVector().subtract(playerEnemy.getLocation().toVector());
		                    	Vector pull = direction.normalize().multiply(strength);
		                    	Vector newVelocity = playerEnemy.getVelocity().multiply(0.3).add(pull.multiply(0.7));
		                    	playerEnemy.setVelocity(newVelocity);
	                    	}
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
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   §f▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + namePassif + "§f, .");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameFirstSpell + "§f, . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameSecondSpell + "§f, . §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
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
