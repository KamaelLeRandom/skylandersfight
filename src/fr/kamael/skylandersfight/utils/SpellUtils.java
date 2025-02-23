package fr.kamael.skylandersfight.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.runnable.ParticleRunnable;
import fr.kamael.skylandersfight.utils.runnable.SkylanderDamageRunnable;

public class SpellUtils {
	
	public static void heal(Skylander skylander, Double value) {
		Player player = skylander.getPlayer();
		
		if (skylander.checkStatus(Status.NOHEAL)) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous êtes sous §cHémorragie§f, vous ne pouvez pas vous soigner pour le moment.");
		} else {
			player.getWorld().spawnParticle(Particle.HEART, player.getEyeLocation().getX(), player.getEyeLocation().getY() + 0.5, player.getEyeLocation().getZ(), 10, 0., 0., 0.);
			player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);

			if (player.getHealth() + value >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
			} else {
				player.setHealth(player.getHealth() + value);
			}
		}
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
}
