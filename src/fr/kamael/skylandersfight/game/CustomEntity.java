package fr.kamael.skylandersfight.game;

import org.bukkit.entity.Entity;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class CustomEntity {
	protected Plugin plugin = Plugin.plugin;
	protected Skylander skylander;
	protected Entity entity;
	
	public CustomEntity(Skylander skylander) {
		this.skylander = skylander;
		
		summon();
		
		// TODO : Ajouter l'entité dans les attributs de l'Arena.
	}
	
	public Skylander getSkylander() {
		return this.skylander;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	
	public void removeEntity() {
		// TODO : Retirer l'entité dans les attributs de l'Arena.

		entity.remove();
		entity = null;		
	}
	
	/// --- Méthodes à surcharger.
	
	public void summon() { return; }
	
	public void onHit(Skylander skylander) { return; }
	
	public void onDamage(Skylander skylander) { return; }
	
	public void onDeath() { return; }
}
