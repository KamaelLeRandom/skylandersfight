package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;

public class SprocketMinecart extends CustomEntity {

	public SprocketMinecart(Sprocket sprocket, Location location) {
		super(sprocket, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		player.setAllowFlight(true);
		Minecart minecart = (Minecart) player.getWorld().spawnEntity(location, EntityType.MINECART);
		minecart.addPassenger(player);
		minecart.setInvulnerable(true);
		
		new BukkitRunnable() {
			private Integer timer = Sprocket.durationTickFirstSpell;
			
			@Override
			public void run() {
				if (timer == 0 || !plugin.game.isState(GameState.FIGHTING) || !skylander.isAlive()) {
					if (!minecart.isDead())
						minecart.removePassenger(player);
					player.setAllowFlight(false);
					removeEntity();
					cancel();
					return;
				}
				
				if (minecart.getPassengers().contains(player)) {
					minecart.setVelocity(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.9)).getDirection().normalize());
				} else {
					player.setAllowFlight(false);
					removeEntity();
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 1);
		
		this.entity = minecart;
	}
}
