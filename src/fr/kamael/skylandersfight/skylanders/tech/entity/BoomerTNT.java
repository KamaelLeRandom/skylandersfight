package fr.kamael.skylandersfight.skylanders.tech.entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Boomer;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class BoomerTNT extends CustomEntity {
	
	public BoomerTNT(Boomer boomer, Location location) {
		super(boomer, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.2)), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(100);
        tnt.setVelocity(player.getLocation().getDirection().multiply(1.5));
        
        new BukkitRunnable() {
        	private Integer timer = 50;
        	private Boolean hit = false;
        	
            @Override
            public void run() {
                if (hit || timer == 0 || tnt == null || tnt.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
                	removeEntity();
                    cancel();
                    return;
                }
                
                timer--;

                ArrayList<Skylander> skylanderHits = SpellUtils.skylanderAround(plugin, skylander, tnt.getLocation(), Boomer.rangeThrowPassif, Boomer.rangeThrowPassif, Boomer.rangeThrowPassif);
                
                if (timer == 0 || skylanderHits.size() > 0) {
                	tnt.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, tnt.getLocation(), 1);
                	
                	for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, tnt.getLocation(), Boomer.rangeExplosePassif, Boomer.rangeExplosePassif, Boomer.rangeExplosePassif)) {
    					Player playerHit = skylanderHit.getPlayer();
    					playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    					playerHit.damage(Boomer.damagePassif, player);
                	}
                	
                	removeEntity();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
		
		this.entity = tnt;
	}
}
