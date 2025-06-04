package fr.kamael.skylandersfight.skylanders.eau.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.eau.Chill;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class ChillFish extends CustomEntity {

	public ChillFish(Chill Chill, Location location) {
		super(Chill, location);
	}
	
	@Override
	public void summon() {
		Player player = this.skylander.getPlayer();
		
		Guardian guardian = (Guardian) player.getWorld().spawnEntity(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2)), EntityType.GUARDIAN);
		guardian.setHealth(0.1);
		guardian.setGravity(false);
		guardian.setVelocity(player.getLocation().getDirection().clone().multiply(2).normalize());
		
		new BukkitRunnable() {
			private Integer timer = 200;
			private Boolean hit = false;
			
			@Override
			public void run() {
				if (timer == 0 || hit ||  !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					removeEntity();
					cancel();
					return;
				}
				
				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, guardian.getLocation(), Chill.rangeFirstSpell, Chill.rangeFirstSpell, Chill.rangeFirstSpell)) {
					Player playerHit = skylanderHit.getPlayer();
					playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1, 1);
					playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence " + Chill.nameFirstSpell + "§f de §e" + player.getName() + "§f.");
					playerHit.damage(Chill.damageFirstSpell, player);
					
					player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Vous venez de touché §e" + playerHit.getName() + "§f avec votre compétence " + Chill.nameFirstSpell + "§f.");
					
					hit = true;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 2);
		
		this.entity = guardian;
	}
}
