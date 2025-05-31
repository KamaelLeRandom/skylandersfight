package fr.kamael.skylandersfight.skylanders.vie.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class ZooLouPig extends CustomEntity {
	private Integer numberOfJump = 3;

	public ZooLouPig(ZooLou zoolou, Location location) {
		super(zoolou, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		skylander.addStatus(null, Status.NOFALL);
		
		Pig pig = (Pig) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
		pig.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(500);
		pig.setHealth(500);
		pig.setCollidable(false);
        pig.setSaddle(true);
        pig.addPassenger(player);
        
        new BukkitRunnable() {
        	private Integer timer = 150;
			
			@Override
			public void run() {
				if (timer == 0 || pig == null || pig.getPassengers().size() == 0 || pig.isDead() || !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					((ZooLou) skylander).secondSpell_Reset();
					skylander.removeStatus(Status.NOFALL);
					removeEntity();
					cancel();
					return;
				}
				
				if (pig.isOnGround()) {
					pig.setVelocity(player.getVelocity().multiply(15.));
				}
					
				timer--;
			}
		}.runTaskTimer(plugin, 0, 2);
		
		this.entity = pig;
	}
	
	@Override
	public Boolean onFall() {
		Double radius = 4.;
		Double y = 0.2;
		
		for (double t = 0; t < 50; t += 0.5) {
			Double x = radius * (float) Math.sin(t);
			Double z = radius * (float) Math.cos(t);
			Location locParticule = entity.getLocation().clone().add(x, y, z);
			entity.getWorld().spawnParticle(Particle.SMOKE_LARGE, locParticule, 0, 0., 0., 0.);
		}
		
		for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this.skylander, entity.getLocation(), 3.5, 1.5, 3.5)) {
			Player playerHit = skylanderHit.getPlayer();
			playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
			playerHit.sendMessage(Constants.prefixMessage + "Vous venez de vous faire écraser par le "+ ZooLou.nameSecondSpell +"§f de §a"+ skylander.getPlayer().getName() +"§f.");
			playerHit.damage(ZooLou.damagePigSecondSpell, skylander.getPlayer());
			if (!playerHit.getLocation().getBlock().getType().isSolid())  
				playerHit.teleport(playerHit.getLocation().clone().subtract(0, 1, 0));
		}
		
		return true; 
	}
	
	public void jump() {
		if (numberOfJump > 0) {
			numberOfJump--;
			this.entity.setVelocity(new Vector(0, 0, 0));

			new BukkitRunnable() {
				
				@Override
				public void run() {
					entity.setVelocity(new Vector(0, 1., 0));
					cancel();
					return;
				}
			}.runTaskLater(plugin, 2);
		}
	}
}
