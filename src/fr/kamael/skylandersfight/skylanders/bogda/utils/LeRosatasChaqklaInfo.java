package fr.kamael.skylandersfight.skylanders.bogda.utils;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.bogda.LeRosatas;

public class LeRosatasChaqklaInfo {
	private Double maxHealth;
	private Double health;
	private Double force;
	private Double resis;
	private Location location;
	
	public LeRosatasChaqklaInfo(LeRosatas lerosatas) {
		Player player = lerosatas.getPlayer();
		maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		health = player.getHealth();
		location = player.getLocation();
		force = lerosatas.getForce();
		resis = lerosatas.getResis();
	}

	public Double getMaxHealth() {
		return maxHealth;
	}

	public Double getHealth() {
		return health;
	}

	public Double getForce() {
		return force;
	}

	public Double getResis() {
		return resis;
	}

	public Location getLocation() {
		return location;
	}
}
