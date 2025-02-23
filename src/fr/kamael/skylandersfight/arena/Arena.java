package fr.kamael.skylandersfight.arena;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.arena.entity.ArenaCorpse;
import fr.kamael.skylandersfight.arena.entity.ArenaItem;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class Arena {
	protected Plugin plugin = Plugin.plugin;
	protected String name;
	protected ArrayList<Element> elements = new ArrayList<Element>();
	protected ArrayList<Location> playerSpawns = new ArrayList<Location>();
	protected ArrayList<Location> itemSpawns = new ArrayList<Location>();
	protected ArrayList<Location> healSpawns = new ArrayList<Location>();
	protected ArrayList<ArenaCorpse> corpse = new ArrayList<ArenaCorpse>();
	protected ArrayList<ArenaItem> items = new ArrayList<ArenaItem>();
	protected ArrayList<CustomEntity> entites = new ArrayList<CustomEntity>();
	
	public String getName() {
		return this.name;
	}
	
	public Element getRandomElement() {
		return this.elements.get(plugin.random.nextInt(this.elements.size()));
	}
	
	public Location getRandomPlayerSpawn() {
		return this.playerSpawns.get(plugin.random.nextInt(this.playerSpawns.size()));
	}
	
	public Location getRandomItemSpawn() {
		return this.itemSpawns.get(plugin.random.nextInt(this.itemSpawns.size()));
	}
	
	public ArenaCorpse summonArenaCorpse(Skylander skylander, Location location) {
		ArenaCorpse corpse = new ArenaCorpse(skylander, location);
		this.corpse.add(corpse);
		return corpse;
	}
	
	public void removeArenaCorpse(Skylander skylander) {
		for (ArenaCorpse corpse : this.corpse) {
			if (corpse.getSkylander().equals(skylander)) {
				corpse.removeEntity();
				return;
			}
		}
	}
	
	public void removeAllArenaCorpse() {
		for (ArenaCorpse corpse : this.corpse) {
			corpse.removeEntity();
		}
		this.corpse.removeAll(this.corpse);
		return;
	}

	public ArenaItem summonArenaItem() {
		Bukkit.broadcastMessage(Constants.prefixMessage + "Un §eObjet Aléatoire§f est apparu dans l'arène !");
		ArenaItem item = new ArenaItem(getRandomItemSpawn());
		this.items.add(item);		
		return item;
	}
	
	public void removeArenaItem(ArenaItem item) {
		item.removeEntity();
		this.items.remove(item);
		return;
	}
	
	public void removeAllArenaItem() {
		for (ArenaItem items : this.items) {
			items.removeEntity();
		}
		this.items.removeAll(this.items);
		return;
	}

	public void addCustomEntity(CustomEntity entity) {
		this.entites.add(entity);
	}
	
	public CustomEntity isCustomEntity(Entity entity) {
		ArrayList<CustomEntity> allEntities = new ArrayList<>();
		allEntities.addAll(this.entites);
		allEntities.addAll(this.items);
		allEntities.addAll(this.corpse);
		for (CustomEntity customEntity : allEntities) {
			if (entity.equals(customEntity.getEntity())) {
				return customEntity;
			}
		}
		return null;
	}
	
	public void removeCustomEntity(CustomEntity customEntity) {
		customEntity.removeEntity();
		this.entites.remove(customEntity);
		return;
	}
	
	public void removeAllCustomEntities() {
		for (CustomEntity customEntity : this.entites) {
			customEntity.removeEntity();
		}
		this.entites.removeAll(this.entites);
		return;
	}

	public void teleportAllPlayer() {
		Integer index = (int) (Math.random() * playerSpawns.size());
		Integer taille = playerSpawns.size();
		Integer i = 0;
		
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			if (gamePlayer.getSkylander().isAlive()) {
				gamePlayer.getPlayer().teleport(playerSpawns.get((index+i)%taille));
			}
			i++;
		}
	}
	
	public void resetHeal() {
		for (Location location : healSpawns) {
			location.getBlock().setType(Material.EMERALD_BLOCK);
		}
	}

	/// --- Méthodes à surcharger.
	
	public void event() { return; }
}
