package fr.kamael.skylandersfight.skylanders.magie.entity;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class SpyroFireball extends CustomEntity {
	public static final Integer fireTickFireball = 60;
	public static final Double damageFireball = 5.;
	public static final Double rangeFireball = 3.;
	public static final Double speedFireball = 0.6;
	
	public SpyroFireball(Spyro skylander, Location location) {
		super(skylander, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		World world = player.getWorld();
        Fireball fireball = world.spawn(location, Fireball.class);
        fireball.setFireTicks(300);
        fireball.setIsIncendiary(false);
        fireball.setShooter(player);
        fireball.setVelocity(player.getLocation().getDirection().clone());
        
        this.entity = fireball;
        
        new BukkitRunnable() {
			private Integer maxTicksAlive = 250;
			private Location fireballLoc = null;
			private Skylander skylanderNearby = null;
			private ArrayList<Skylander> skylandersHit = null;
        	
			@Override
			public void run() {
				if (maxTicksAlive <= 0 || entity == null || entity.isDead() || entity.getLocation().getBlock().getType().isSolid() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				fireballLoc = entity.getLocation().clone();
				
				skylanderNearby = SpellUtils.nearClosePlayer(plugin, skylander, fireball, 50.);
				if (skylanderNearby != null)
					fireball.setVelocity(skylanderNearby.getPlayer().getLocation().toVector()
							.subtract(fireballLoc.toVector())
							.normalize()
							.multiply(speedFireball));
				
				skylandersHit = SpellUtils.skylanderAround(plugin, skylander, fireballLoc, rangeFireball, rangeFireball, rangeFireball);
				if (skylandersHit.size() > 0) 
					explose(skylandersHit);
						
				fireballLoc.getWorld().spawnParticle(Particle.FLAME, fireballLoc, 2, 0.1, 0.1, 0.1, 0.01);
				fireballLoc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, fireballLoc, 2, 0.1, 0.1, 0.1, 0.01);
				fireballLoc.getWorld().spawnParticle(Particle.CRIT_MAGIC, fireballLoc, 1, 0.05, 0.05, 0.05, 0.01);
				fireballLoc.getWorld().spawnParticle(Particle.REDSTONE, fireballLoc, 1, 0.05, 0.05, 0.05, 0, new Particle.DustOptions(Color.fromRGB(255, 50, 0), 1.5f));
				
				maxTicksAlive--;
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	@Override
	public void onHit(Skylander skylander) { 
		explose(SpellUtils.skylanderAround(plugin, skylander, entity.getLocation(), rangeFireball, rangeFireball, rangeFireball));
		return; 
	}

	private void explose(ArrayList<Skylander> skylandersHit) {
		Player player = skylander.getPlayer();
		player.sendMessage(Constants.prefixMessage + "Vous venez de toucher §6" + skylandersHit.size() + " joueur(s)§f avec votre compétence " + Spyro.nameSecondSpell + "§f.");
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
		player.spawnParticle(Particle.EXPLOSION_NORMAL, entity.getLocation(), 1);
		
		for (Skylander skylanderHit : skylandersHit) {
			Player playerHit = skylanderHit.getPlayer();
			playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence " + Spyro.nameSecondSpell + "§f de §6" + player.getName() + "§f");
			playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			playerHit.setFireTicks(playerHit.getFireTicks() + fireTickFireball);
			playerHit.damage(damageFireball, player);
		}	
	}
}
