package fr.kamael.skylandersfight.arena.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.skylanders.Element;

public class VestigesAzteques extends Arena {
	public static final String nameArena = "§aVestiges Aztéques";
	public static final String nameEvent = "§aRessources Antiques";
	public static final Double pourcentPlatform = 0.3;
	public static final Integer timerRemove = 5;
	public static final Integer timerRecreate = 5;
	
	private ArrayList<Location> platformsLocation = new ArrayList<Location>();

	public VestigesAzteques() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -530.5, 4.5, -1182.5));
		this.playerSpawns.add(new Location(w, -496.5, 4.5, -1163.5));
		this.playerSpawns.add(new Location(w, -487.5, 4.5, -1131.5));
		this.playerSpawns.add(new Location(w, -496.5, 4.5, -1099.5));
		this.playerSpawns.add(new Location(w, -534.5, 4.5, -1078.5));
		this.playerSpawns.add(new Location(w, -562.5, 4.5, -1095.5));
		this.playerSpawns.add(new Location(w, -590.5, 4.5, -1140.5));
		this.playerSpawns.add(new Location(w, -566.5, 4.5, -1177.5));
		
		this.healSpawns.add(new Location(w, -531, 13, -1139)); // Fer
		this.healSpawns.add(new Location(w, -531, 14, -1120)); // Or
		this.healSpawns.add(new Location(w, -548, 13, -1121)); // Diamant
		this.healSpawns.add(new Location(w, -546, 13, -1140)); // Copper
		
		this.itemSpawns.add(new Location(w, -532.5, 13.5, -1130.5)); // Interieur 1
		this.itemSpawns.add(new Location(w, -546.5, 13.5, -1131.5)); // Interieur 2
		this.itemSpawns.add(new Location(w, -537.5, 13.5, -1366.5)); // Interieur 3
		this.itemSpawns.add(new Location(w, -519.5,  9.5, -1149.5)); // Extérieur 1
		this.itemSpawns.add(new Location(w, -559.5,  9.5, -1149.5)); // Extérieur 2 
		this.itemSpawns.add(new Location(w, -559.5,  9.5, -1109.5)); // Extérieur 3
		this.itemSpawns.add(new Location(w, -519.5,  9.5, -1109.5)); // Extérieur 4
		
		this.platformsLocation.add(new Location(w, -555, 2, -1115));
		this.platformsLocation.add(new Location(w, -549, 3, -1115));
		this.platformsLocation.add(new Location(w, -540, 3, -1115));
		this.platformsLocation.add(new Location(w, -534, 4, -1115));
		this.platformsLocation.add(new Location(w, -528, 3, -1115));
		this.platformsLocation.add(new Location(w, -525, 4, -1120));
		this.platformsLocation.add(new Location(w, -530, 5, -1119));
		this.platformsLocation.add(new Location(w, -536, 2, -1119));
		this.platformsLocation.add(new Location(w, -543, 4, -1119));
		this.platformsLocation.add(new Location(w, -548, 5, -1119));
		this.platformsLocation.add(new Location(w, -553, 3, -1120));
		this.platformsLocation.add(new Location(w, -550, 6, -1123));
		this.platformsLocation.add(new Location(w, -555, 4, -1125));
		this.platformsLocation.add(new Location(w, -545, 7, -1124));
		this.platformsLocation.add(new Location(w, -540, 8, -1125));
		this.platformsLocation.add(new Location(w, -539, 5, -1122));
		this.platformsLocation.add(new Location(w, -533, 6, -1124));
		this.platformsLocation.add(new Location(w, -554, 5, -1130));
		this.platformsLocation.add(new Location(w, -550, 7, -1129));
		this.platformsLocation.add(new Location(w, -545, 8, -1130));
		this.platformsLocation.add(new Location(w, -555, 5, -1136));
		this.platformsLocation.add(new Location(w, -550, 6, -1135));
		this.platformsLocation.add(new Location(w, -545, 7, -1135));
		this.platformsLocation.add(new Location(w, -540, 8, -1135));
		this.platformsLocation.add(new Location(w, -535, 7, -1135));
		this.platformsLocation.add(new Location(w, -535, 8, -1130));
		this.platformsLocation.add(new Location(w, -530, 7, -1128));
		this.platformsLocation.add(new Location(w, -525, 5, -1129));
		this.platformsLocation.add(new Location(w, -529, 6, -1133));
		this.platformsLocation.add(new Location(w, -525, 5, -1137));
		this.platformsLocation.add(new Location(w, -529, 4, -1141));
		this.platformsLocation.add(new Location(w, -525, 3, -1145));
		this.platformsLocation.add(new Location(w, -531, 2, -1145));
		this.platformsLocation.add(new Location(w, -533, 5, -1139));
		this.platformsLocation.add(new Location(w, -538, 7, -1140));
		this.platformsLocation.add(new Location(w, -535, 6, -1144));
		this.platformsLocation.add(new Location(w, -540, 5, -1145));
		this.platformsLocation.add(new Location(w, -543, 6, -1140));
		this.platformsLocation.add(new Location(w, -547, 5, -1140));
		this.platformsLocation.add(new Location(w, -552, 4, -1140));
		this.platformsLocation.add(new Location(w, -548, 4, -1145));
		this.platformsLocation.add(new Location(w, -554, 5, -1145));
		
		this.elements.add(Element.VIE);
		this.elements.add(Element.MORT);
	}
	
	@Override
	public void onStart() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(16000);
		
		rebuildAllPlatforms();
	}
		
	@Override
	public void event() {
	    new BukkitRunnable() {
	        private final Set<Location> transitionPlatform = new HashSet<>();

	        @Override
	        public void run() {
	            List<Location> availables = platformsLocation.stream()
	                .filter(loc -> !transitionPlatform.contains(loc))
	                .collect(Collectors.toList());

	            if (availables.isEmpty()) return;

	            Collections.shuffle(availables);
	            Integer number = (int) Math.ceil(pourcentPlatform * availables.size());
	            List<Location> aDisparaitre = availables.subList(0, Math.min(number, availables.size()));

	            transitionPlatform.addAll(aDisparaitre);

	            // Étape 1 : avertissement -> stone → cobblestone
	            Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                for (Location center : aDisparaitre) {
	                    changeStoneIntoCobble(center);
	                }
	            }, (timerRemove - 2) * 20);

	            // Étape 2 : disparition
	            Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                for (Location center : aDisparaitre) {
	                    removeOnePlatform(center);
	                }
	            }, timerRemove * 20);

	            // Étape 3 : réapparition
	            Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                for (Location center : aDisparaitre) {
	                    rebuildOnePlatform(center);
	                    transitionPlatform.remove(center);
	                }
	            }, (timerRemove + timerRecreate) * 20);
	        }
	    }.runTaskTimer(plugin, 0, 100);
	}
	
	private void changeStoneIntoCobble(Location center) {
	    Location base = center.clone().getBlock().getLocation();
	    
	    for (int x = -1; x <= 1; x++) {
	        for (int z = -1; z <= 1; z++) {
	            if (x == 0 && z == 0) continue;
	            Location blockLoc = base.clone().add(x, 0, z);
	            blockLoc.getBlock().setType(Material.COBBLESTONE);
	        }
	    }
	}
	
	public void removeOnePlatform(Location center) {
	    Location base = center.clone().getBlock().getLocation();

	    for (int x = -1; x <= 1; x++) {
	        for (int z = -1; z <= 1; z++) {
	            Location blockLoc = base.clone().add(x, 0, z);
	            blockLoc.getBlock().setType(Material.AIR);
	        }
	    }
	}
	
	private void rebuildOnePlatform(Location center) {
	    Location base = center.clone().getBlock().getLocation();
	    
	    for (int x = -1; x <= 1; x++) {
	        for (int z = -1; z <= 1; z++) {
	            Location blockLoc = base.clone().add(x, 0, z);
	            if (x == 0 && z == 0) {
	                blockLoc.getBlock().setType(Material.SEA_LANTERN);
	            } else {
	                blockLoc.getBlock().setType(Material.STONE);
	            }
	        }
	    }
	}
	
	private void rebuildAllPlatforms() {
	    for (Location platform : platformsLocation) {
	        rebuildOnePlatform(platform);
	    }
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§7Bonus Élémentaire : §7"+ Element.VIE.getName() + " | " + Element.MORT.getName(),
			"§fPour accèder aux "+ nameEvent + "§f, vous devez passer par le centre du temple, en parcourant les platformes qui disparaissent et réapparaisent."
		));
		ItemStack item = new ItemStack(Material.MOSS_BLOCK, 1);
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
