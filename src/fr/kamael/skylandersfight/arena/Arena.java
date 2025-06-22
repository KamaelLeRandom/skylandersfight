package fr.kamael.skylandersfight.arena;

import java.util.ArrayList;
import java.util.Collections;

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
	protected Boolean deathmatch = false;
	protected Integer secondMinRespawnHeal = 30;
	protected Integer secondMaxRespawnHeal = 60;
	protected Integer secondMinRespawnItem = 60;
	protected Integer secondMaxRespawnItem = 90;
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
	
	public Integer getRandomTimerRespawnHeal() {
		return plugin.random.nextInt((secondMaxRespawnHeal - secondMinRespawnHeal) + 1) + secondMinRespawnHeal;
	}
	
	public Integer getRandomTimerRespawnItem() {
		return plugin.random.nextInt((secondMaxRespawnItem - secondMinRespawnItem) + 1) + secondMinRespawnItem;
	}
	
	public String getResumeElement() {
	    StringBuilder resume = new StringBuilder();
	    for (Element element : elements) {
	        if (resume.length() > 0) {
	            resume.append(", ");
	        }
	        resume.append(element.getName());
	    }
	    return resume.toString();
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
			if (corpse.getEntity() != null) {
				corpse.removeEntity();	
			}
		}
		this.corpse.removeAll(this.corpse);
		return;
	}

	@SuppressWarnings("unchecked")
	public void summonArenaItem() {
		items.removeIf(i -> i.getEntity() == null || i.getEntity().isDead());
		
		ArrayList<Location> locationToTest = (ArrayList<Location>) itemSpawns.clone();
		Collections.shuffle(locationToTest);
		
	    Location validLocation = null;

	    for (Location location : locationToTest) {
	        Boolean alreadyOccuped = false;

	        for (ArenaItem existingItem : items) {
	            if (existingItem.getEntity().getLocation().distance(location) < 1.0) {
	            	alreadyOccuped = true;
	                break;
	            }
	        }

	        if (!alreadyOccuped) {
	            validLocation = location;
	            break;
	        }
	    }

	    if (validLocation == null) {
	        Bukkit.broadcastMessage(Constants.prefixMessage + "Un §eObjet Aléatoire§f n'est pas apparu car il n'y a plus d'emplacement libre.");
	        return;
	    } else {
		    Bukkit.broadcastMessage(Constants.prefixMessage + "Un §eObjet Aléatoire§f est apparu dans l'arène !");
		    items.add(new ArenaItem(validLocation));
		    return;
	    }
	}
	
	public void removeArenaItem(ArenaItem item) {
		this.items.remove(item);
		return;
	}
	
	public void removeAllArenaItem() {
		for (ArenaItem items : this.items) {
			if (items.getEntity() != null) {
				items.removeEntity();
			}
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
			if (customEntity.getEntity() != null) {
				customEntity.removeEntity();
			}
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
	
	public void deathmatch() { return; }
	
	public void event() { return; }
	
	public Boolean onSummonItem() { return true; }
	
	public void onStart() { return; }
	
	public void onKill(Skylander skylanderKiller, Skylander skylanderDeath) { return; }
}
