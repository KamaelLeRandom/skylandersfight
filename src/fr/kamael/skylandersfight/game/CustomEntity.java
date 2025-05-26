package fr.kamael.skylandersfight.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class CustomEntity {
	protected Plugin plugin = Plugin.plugin;
	protected Skylander skylander;
	protected Location location;
	protected Entity entity;
	
	public CustomEntity(Skylander skylander, Location location) {
		Bukkit.broadcastMessage("DEBUG : constructor");
		this.skylander = skylander;
		this.location = location;
		
		summon();
		
		plugin.game.getRound().getArena().addCustomEntity(this);
	}
	
	public Skylander getSkylander() {
		return this.skylander;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	
	public void removeEntity() {
		if (entity != null)
			entity.remove();
		entity = null;		
	}
	
	/// --- Méthodes à surcharger.
	
	public void summon() { return; }
	
	public void onHit(Skylander skylander) { return; }
	
	public void onDamage(Skylander skylander) { return; }
	
	public void onDeath() { return; }
}
