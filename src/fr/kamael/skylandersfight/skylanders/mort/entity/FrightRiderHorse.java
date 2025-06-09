package fr.kamael.skylandersfight.skylanders.mort.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.mort.FrightRider;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class FrightRiderHorse extends CustomEntity {
	public static final Integer health = 10;
	public static final Double rangeWither = 5.;
	
	public FrightRiderHorse(FrightRider frightrider, Location location) {
		super(frightrider, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		SkeletonHorse horse = (SkeletonHorse) player.getWorld().spawnEntity(location, EntityType.SKELETON_HORSE);
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
		horse.setHealth(health);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
		horse.addPassenger(player);
		horse.setOwner(player);
		horse.setTamed(true);
		horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
		horse.setLootTable(null);
		
		this.entity = horse;
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (entity == null || entity.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					removeEntity();
					cancel();
					return;
				}
				
				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, horse.getLocation(), rangeWither, 2., rangeWither)) {
					Player playerHit = skylanderHit.getPlayer();
					
					if (playerHit.hasPotionEffect(PotionEffectType.WITHER)) {
					    PotionEffect currentWither = playerHit.getPotionEffect(PotionEffectType.WITHER);
					    
					    if (currentWither == null || currentWither.getDuration() < 200) {
					        playerHit.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40 + currentWither.getDuration(), 0, false, false));
					    }
					} else {
						playerHit.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0, false, false));
					}
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	@Override
	public void onDeath() {
		((FrightRider) skylander).removePassif();
		skylander.addCooldown(FrightRider.namePassif, FrightRider.timerPassif);
		return; 
	}
	
	public void invisibility() {
		SkeletonHorse horse = (SkeletonHorse) entity;
		horse.setInvisible(true);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (horse == null || horse.isDead() || !skylander.isAlive() || !skylander.checkStatus(Status.INVISIBLE) || !plugin.game.isState(GameState.FIGHTING)) {
					horse.setInvisible(false);
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}
}
