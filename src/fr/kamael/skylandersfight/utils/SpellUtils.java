package fr.kamael.skylandersfight.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.manager.ItemManager;
import fr.kamael.skylandersfight.utils.manager.NPCManager;
import fr.kamael.skylandersfight.utils.manager.NPCManager.Hand;
import fr.kamael.skylandersfight.utils.manager.NPCManager.NPCMetaData;
import fr.kamael.skylandersfight.utils.manager.NPCManager.SkinTextures;
import fr.kamael.skylandersfight.utils.runnable.ParticleRunnable;
import fr.kamael.skylandersfight.utils.runnable.SkylanderDamageRunnable;

public class SpellUtils {
	
	public static Boolean changeLife(Skylander skylander, Double value) {
		Player player = skylander.getPlayer();
		Double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		
		if (maxHealth + value > 2.) {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + value);
			return true;
		} else {
			return false;
		}
	}
	
	public static void heal(Skylander skylander, Double value, Boolean animation) {
		Player player = skylander.getPlayer();
		
		if (skylander.checkStatus(Status.NOHEAL)) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous êtes sous §cHémorragie§f, vous ne pouvez pas vous soigner pour le moment.");
		} else {
			if (player.getHealth() + value >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			} else {
				player.setHealth(player.getHealth() + value);
			}
			
			if (animation) {
				player.getWorld().spawnParticle(Particle.HEART, player.getEyeLocation().getX(), player.getEyeLocation().getY() + 0.5, player.getEyeLocation().getZ(), 10, 0., 0., 0.);
				player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
			}
		}
	}
	
	public static void invulnerability(Plugin plugin, Skylander skylander, Integer ticks) {
		Player player = skylander.getPlayer();
		ItemStack boots = player.getInventory().getBoots();
		ItemStack leggi = player.getInventory().getLeggings();
		ItemStack chest = player.getInventory().getChestplate();
		ItemStack helme = player.getInventory().getHelmet();
		
		player.getInventory().setHelmet(ItemManager.makeBasicItem(Material.GOLDEN_HELMET, "", 1));
		player.getInventory().setChestplate(ItemManager.makeBasicItem(Material.GOLDEN_CHESTPLATE, "", 1));
		player.getInventory().setLeggings(ItemManager.makeBasicItem(Material.GOLDEN_LEGGINGS, "", 1));
		player.getInventory().setBoots(ItemManager.makeBasicItem(Material.GOLDEN_BOOTS, "", 1));
		
		skylander.addStatus(null, Status.NOTAKEDAMAGE);
		
		new BukkitRunnable() {
			private Integer timer = ticks;
			@Override
			public void run() {
				// Condition d'arrêt.
				if (timer == 0) {
					player.getInventory().setHelmet(helme);
					player.getInventory().setChestplate(chest);
					player.getInventory().setLeggings(leggi);
					player.getInventory().setBoots(boots);
					skylander.removeStatus(Status.NOTAKEDAMAGE);
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public static void invisibility(Plugin plugin, Skylander skylander, Integer ticks) {
		Player player = skylander.getPlayer();
		
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Player playerOther = gamePlayer.getPlayer();
			
			if (! playerOther.equals(player)) {
				playerOther.hidePlayer(plugin, player);
			}
		}
		
		skylander.addStatus(ticks, Status.INVISIBLE);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (! skylander.checkStatus(Status.INVISIBLE)) {
					for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
						Player playerOther = gamePlayer.getPlayer();
						
						if (! playerOther.equals(player)) {
							playerOther.showPlayer(plugin, player);
						}
					}
					
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	public static Skylander targetPlayer(Skylander skylander, Integer distance, ParticleRunnable particule) {
		Player player = skylander.getPlayer();
		Location eyeLocation = player.getEyeLocation();
	    Vector direction = eyeLocation.getDirection();
		
		for (int i = 2; i<=distance; i++) {
	        Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(i));
	        
	        if (particule != null) {
	        	particule.execute(checkLocation);
	        }
			
			if (TraversableBlocksUtils.isTraversableBlock(player.getWorld().getBlockAt(checkLocation).getType())) {
				for (Entity entity : player.getWorld().getNearbyEntities(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(i)), 0.75, 0.75, 0.75)) {
					if (entity instanceof Player && entity != player) {
						Player playerTarget = (Player) entity;
						Skylander skylanderTarget = Plugin.plugin.game.getPlayer(playerTarget).getSkylander();
						
						if (skylanderTarget.isAlive() && !skylander.getMates().contains(skylanderTarget)) {
							return skylanderTarget;
						}
					}
				}
			} else {
				return null;
			}
		}
		
		return null;
	}
	
	public static ArrayList<Skylander> skylanderAround(Plugin plugin, Skylander skylander, Location location, Double x, Double y, Double z) {
		ArrayList<Skylander> skylandersHit = new ArrayList<>();
		
		for (Entity entity : location.getWorld().getNearbyEntities(location, x, y, z)) {
			if (entity instanceof Player && entity != skylander.getPlayer()) {
				Player playerHit = (Player) entity;
				Skylander skylanderHit = plugin.game.getPlayer(playerHit).getSkylander();
				
				if (skylanderHit.isAlive() && !skylander.getMates().contains(skylanderHit)) {
					skylandersHit.add(skylanderHit);
				}
			}
		}
		
		return skylandersHit;
	}
	
	public static void dash(Skylander skylander, Double value, SkylanderDamageRunnable damageCallback, ParticleRunnable particleCallback) {
		Plugin plugin = Plugin.plugin;
		Player player = skylander.getPlayer();
		
		Vector direction = player.getLocation().getDirection();
		Vector dashVector = direction.multiply(value);
		player.setVelocity(dashVector);
		
		new BukkitRunnable() {
			private Integer timer = 10;
			private ArrayList<Player> listPlayer = new ArrayList<Player>();

			@Override
			public void run() {
				particleCallback.execute(player.getLocation());
				
				for (Entity entity : player.getNearbyEntities(3, 2, 3)) {
					if (entity instanceof Player && entity != player && !listPlayer.contains(entity)) {
						Player playerHit = (Player) entity;
						Skylander skylanderHit = plugin.game.getPlayer(playerHit).getSkylander();

						if (skylanderHit.isAlive() && !skylander.getMates().contains(skylanderHit)) {
							damageCallback.execute(skylander, skylanderHit);
						}
					}
				}
				
				if (timer == 0) {
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	public static void tornado(Plugin plugin, Skylander...skylandersHit) {
	    new BukkitRunnable() {
	    	private Integer timer = 20;
	    	
	    	@Override
	        public void run() {
	    		if (timer == 0) {
	    			cancel();
	    			return;
	    		}
	    		
	    		for (Skylander skylanderHit : skylandersHit) {
	                double t = System.currentTimeMillis() % 2000 / 1000.0 * Math.PI * 2;
	                double radius = 0.5;
	                double x = radius * Math.cos(t);
	                double z = radius * Math.sin(t);
	                double y = 0.4;

	                skylanderHit.getPlayer().setVelocity(new Vector(x, y, z));
	    		}
	    		
	    		timer--;
	    	}
	    }.runTaskTimer(plugin, 0, 2);
	}

	public static void fly(Skylander skylander, Double range, SkylanderDamageRunnable damageCallback, ParticleRunnable particleCallback) {
		Plugin plugin = Plugin.plugin;
		Player player = skylander.getPlayer();
		
		ItemStack plume = player.getInventory().getItemInMainHand().clone();
		ItemStack plastron = player.getInventory().getChestplate().clone();
		
		player.getInventory().getItemInMainHand().setType(Material.FIREWORK_ROCKET);
		player.getInventory().getChestplate().setType(Material.ELYTRA);
		player.setGliding(true);
		
		new BukkitRunnable() {
			private ArrayList<Player> listPlayer = new ArrayList<Player>();

			@Override
			public void run() {
				particleCallback.execute(player.getLocation());
				
				for (Entity entity : player.getNearbyEntities(range, 1., range)) {
					if (entity instanceof Player && entity != player && !listPlayer.contains(entity)) {
						Player playerHit = (Player) entity;
						Skylander skylanderHit = plugin.game.getPlayer(playerHit).getSkylander();

						if (skylanderHit.isAlive() && !skylander.getMates().contains(skylanderHit)) {
							damageCallback.execute(skylander, skylanderHit);
						}
					}
				}
				
				if (player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					player.getInventory().addItem(plume);
					player.getInventory().setChestplate(plastron);
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	
	
	public static void createFakePlayer(Plugin plugin, Skylander skylander, Player playerTarget, Integer time, SkylanderDamageRunnable damage) {
		Player player = skylander.getPlayer();
		GameProfile gameProfile = ((CraftPlayer) player).getProfile();
		Property property = gameProfile.getProperties().get("textures").iterator().next();
		
		@SuppressWarnings("unchecked")
		Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
		
		NPCManager npc = new NPCManager(player.getLocation(), player.getDisplayName());
		NPCMetaData meta = npc.getMetadata();
		
		meta.setGravity(false);
		meta.setHand(Hand.RIGHT);
		
		npc.setSkin(new SkinTextures(property.getValue(), property.getSignature()));
		npc.updateMetadata(players);
		npc.spawnNPC(players);
		
		npc.setEquipment(players, NPCManager.ItemSlot.BOOTS, player.getInventory().getBoots().clone());
		npc.setEquipment(players, NPCManager.ItemSlot.LEGGINGS, player.getInventory().getLeggings().clone());
		npc.setEquipment(players, NPCManager.ItemSlot.CHESTPLATE, player.getInventory().getChestplate().clone());
		npc.setEquipment(players, NPCManager.ItemSlot.HELMET, player.getInventory().getHelmet().clone());
		npc.setEquipment(players, NPCManager.ItemSlot.MAIN_HAND, new ItemStack(Material.IRON_SWORD));
		
	    
		new BukkitRunnable() {
			private Integer timer = time;
			@Override
			public void run() {
				// Condition d'arrêt.
				if (timer == 0) {
					damage.execute(null, null);
					
					npc.destroyNPC(players);
					cancel();
					return;
				}
				
				npc.lookAtPlayer(playerTarget, playerTarget);
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
