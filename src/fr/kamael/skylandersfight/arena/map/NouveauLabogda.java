package fr.kamael.skylandersfight.arena.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.entity.ScientifiqueLabogda;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class NouveauLabogda extends Arena {
	public static final String nameArena = "§6Nouveau Labo'gda";
	public static final String nameEvent = "§6Invasion de l'Organisation";
	public static final Double probaEvent = 0.30; 
	public static final Double valueRewardEvent = 0.25;
	
	private ArrayList<Location> mobsLocation = new ArrayList<Location>();
	private ArrayList<CustomEntity> mobs = new ArrayList<CustomEntity>();
	private HashMap<Skylander, Integer> mobsKillSkylanders = new HashMap<Skylander, Integer>();

	public NouveauLabogda() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1412.5, 38.5, -684.5)); // Entrée
		this.playerSpawns.add(new Location(w, -1458.5, 38.5, -662.5)); // Portail Aether
		this.playerSpawns.add(new Location(w, -1493.5, 38.5, -704.5)); // Jardin
		this.playerSpawns.add(new Location(w, -1470.5, 38.5, -734.5)); // Labo Grishka
		this.playerSpawns.add(new Location(w, -1474.5, 21.5, -666.5)); // Cercle Invocation
		this.playerSpawns.add(new Location(w, -1479.5, 21.5, -713.5)); // Portail Nether
		this.playerSpawns.add(new Location(w, -1467.5, 50.5, -713.5)); // Infirmerie
		this.playerSpawns.add(new Location(w, -1475.5, 50.5, -677.5)); // Cafétéria
		
		this.healSpawns.add(new Location(w, -1472, 49, -715)); // Infirmerie 1
		this.healSpawns.add(new Location(w, -1476, 49, -715)); // Infirmerie 2
		this.healSpawns.add(new Location(w, -1444, 49, -712)); // Bureau caché
		this.healSpawns.add(new Location(w, -1413, 40, -688)); // Entrée
		this.healSpawns.add(new Location(w, -1474, 40, -736)); // Vestiaire
		this.healSpawns.add(new Location(w, -1454, 39, -702)); // Potion
		this.healSpawns.add(new Location(w, -1459, 37, -661)); // Portail Aether
		this.healSpawns.add(new Location(w, -1480, 38, -704)); // Jardin
		this.healSpawns.add(new Location(w, -1487, 21, -686)); // Converteur
		this.healSpawns.add(new Location(w, -1478, 20, -682)); // Chambre Froide
		
		this.itemSpawns.add(new Location(w, -1487.0, 38.5, -670.0)); // Labo Igor
		this.itemSpawns.add(new Location(w, -1446.5, 38.5, -679.5)); // Accueil
		this.itemSpawns.add(new Location(w, -1493.5, 37.5, -695.5)); // Jardin
		this.itemSpawns.add(new Location(w, -1462.5, 38.5, -699.5)); // Labo de Grishka
		this.itemSpawns.add(new Location(w, -1461.5, 39.5, -730.5)); // Trou Noir
		this.itemSpawns.add(new Location(w, -1451.5, 5.5, -677.5)); // Dortoir 1
		this.itemSpawns.add(new Location(w, -1455.5, 5.5, -677.5)); // Dortoir 2
		this.itemSpawns.add(new Location(w, -1447.5, 50.5, -691.0)); // Toilette
		this.itemSpawns.add(new Location(w, -1447.5, 50.5, -712.5)); // Bureau Grishka
		this.itemSpawns.add(new Location(w, -1430.5, 50.5, -709.5)); // Bureau Igor
		this.itemSpawns.add(new Location(w, -1465.5, 21.6, 681.9)); // Salle Torture
		
		this.mobsLocation.add(new Location(w, -1418.5, 38.5, -683.0));
		this.mobsLocation.add(new Location(w, -1459.5, 21.5, -695.5));
		this.mobsLocation.add(new Location(w, -1466.0, 38.5, -708.0));
		this.mobsLocation.add(new Location(w, -1472.5, 38.5, -667.5));
		this.mobsLocation.add(new Location(w, -1463.0, 50.5, -701.0));
		
		this.elements.add(Element.TECH);
		this.elements.add(Element.BOGDA);
	}

	@Override
	public void deathmatch() {
		deathmatch = true;
		this.playerSpawns.removeAll(this.playerSpawns);
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1500.5, 3.5, 105.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1490.5, 3.5, 115.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1501.5, 3.5, 117.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1483.5, 3.5, 100.5));
		return;
	}
	
	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(16000);
		
		if (plugin.game.getConfig().getActiveEventMap() && plugin.random.nextFloat() < probaEvent) {
			new BukkitRunnable() {
				private Integer timer = 60 + plugin.random.nextInt(120);
				
				@Override
				public void run() {
					if (!plugin.game.isState(GameState.FIGHTING)) {
						for (CustomEntity entity : mobs)
							entity.removeEntity();
						cancel();
						return;
					}
					
					if (timer == 10) {
						for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
							Skylander skylander = gamePlayer.getSkylander();
							Player player = gamePlayer.getPlayer();
							
							player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
							player.sendTitle(nameEvent, "Des intrus arriveront dans 10s.", 1, 60, 1);
							player.sendMessage(Constants.prefixMessage + "L'évenement " + nameEvent + "§f vient de se déclencher, des intrus vont arriver, celui-ci qui tue le plus d'intrus gagne un bonus.");
							
							if (skylander.isAlive()) 
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, false));
						}
					}
					
					if (timer == 0) {
						for (Location loc : mobsLocation) {
							for (Integer i = 0; i < 1 + plugin.game.getPlayers().size(); i++) {
								mobs.add(new ScientifiqueLabogda(loc));
							}
						}
						
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
		}
	}
	
	public void updateMobKilledBySkylander(Skylander skylander) {
		if (mobsKillSkylanders.containsKey(skylander)) {
			mobsKillSkylanders.replace(skylander, mobsKillSkylanders.get(skylander));
		} else {
			mobsKillSkylanders.put(skylander, 1);
		}
		
		checkMobKilled();
	}
	
	public void checkMobKilled() {
		mobs.removeIf(mob -> mob.getEntity() == null || mob.getEntity().isDead());
		
		if (mobs.size() == 0)
			giveRewardEvent();
	}
	
	public void giveRewardEvent() {
		Skylander skylanderReward = null;
		Integer valueReward = 0;
		
		for (Skylander skylanderCheck : mobsKillSkylanders.keySet()) {
			Integer value = mobsKillSkylanders.get(skylanderCheck);
			if (value > valueReward) {
				skylanderReward = skylanderCheck;
				valueReward = value; 
			}
		}
		
		if (skylanderReward != null) {
			Player playerReward = skylanderReward.getPlayer();
			playerReward.playSound(playerReward.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
			playerReward.sendMessage(Constants.prefixMessage + "Vous avez gagné la récompense de l'évenement " + nameEvent + "§f, vous obtenez §6" + valueRewardEvent*100 + "%§f de §cForce§f et §cRésistance§f supplémentaires.");
			
			Bukkit.broadcastMessage(Constants.prefixMessage + "§6" + playerReward.getName() + "a reussi le plus d'intrus, il gagne donc une récompense de " + valueRewardEvent*100 + "%§f de §cForce§f et §cRésistance§f supplémentaires.");
			
			skylanderReward.updateForce(+valueRewardEvent);
			skylanderReward.updateResis(-valueRewardEvent);
		}
	}

	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§fBonus Élémentaire : §7"+ Element.TECH.getName() + " | " + Element.BOGDA.getName())
		);
		ItemStack item = new ItemStack(Material.WAXED_COPPER_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameArena);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
