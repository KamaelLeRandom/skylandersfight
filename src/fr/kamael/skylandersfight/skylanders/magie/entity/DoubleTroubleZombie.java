package fr.kamael.skylandersfight.skylanders.magie.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.magie.DoubleTrouble;

public class DoubleTroubleZombie extends CustomEntity {

	public DoubleTroubleZombie(DoubleTrouble doubletrouble, Location location) {
		super(doubletrouble, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Zombie zombie = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setBaby();
		zombie.setHealth(10);
	}
}
