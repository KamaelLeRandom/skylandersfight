package fr.kamael.skylandersfight.skylanders;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Skylander {
	protected Player player;
	protected ArrayList<Cooldown> cooldowns;
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void addCooldown(String name, Integer seconds) {
		this.cooldowns.add(new Cooldown(this, name, seconds));
	}
	
	public void removeCooldown(String name) {
		this.cooldowns.removeIf(entry -> entry.getName().equals(name));
	}
	
	public void removeCooldown(Cooldown cooldown) {
		this.cooldowns.remove(cooldown);
	}
	
	public void removeAllCooldown() {
		this.cooldowns.removeAll(cooldowns);
	}
	
	public Boolean checkCooldown(String name, Boolean showTimer) {
		for (Cooldown cooldown : cooldowns) {
			if (cooldown.getName().equals(name)) {
				if (showTimer) {
					cooldown.printCooldown();
				}
				return false;
			}
		}
		return true;
	}
}
