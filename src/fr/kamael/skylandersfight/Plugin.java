package fr.kamael.skylandersfight;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kamael.skylandersfight.arena.ArenaListener;
import fr.kamael.skylandersfight.command.SkylanderCommand;
import fr.kamael.skylandersfight.command.SkylanderTabCompleter;
import fr.kamael.skylandersfight.game.Game;
import fr.kamael.skylandersfight.game.GameListener;
import fr.kamael.skylandersfight.skylanders.air.listener.AirListener;
import fr.kamael.skylandersfight.skylanders.eau.listener.EauListener;
import fr.kamael.skylandersfight.skylanders.feu.listener.FeuListener;
import fr.kamael.skylandersfight.skylanders.magie.listener.MagieListener;
import fr.kamael.skylandersfight.skylanders.mort.listener.MortListener;
import fr.kamael.skylandersfight.skylanders.tech.listener.TechListener;
import fr.kamael.skylandersfight.skylanders.terre.listener.TerreListener;
import fr.kamael.skylandersfight.skylanders.vie.listener.VieListener;
import fr.kamael.skylandersfight.utils.PlayerUtils;
import fr.kamael.skylandersfight.utils.StatistiqueUtils;
import fr.kamael.skylandersfight.utils.manager.TeamManager;

public class Plugin extends JavaPlugin {
	public static Plugin plugin;
	public Random random;
	public Game game;
	public StatistiqueUtils statsUtils;
	public PlayerUtils playerUtils;

	/// --- Méthodes initial.
	@Override
	public void onEnable() {
		plugin = this;
		random = new Random();
		statsUtils = new StatistiqueUtils();
		playerUtils = new PlayerUtils();

        getCommand("skylander").setExecutor(new SkylanderCommand());
        getCommand("skylander").setTabCompleter(new SkylanderTabCompleter());
		
		getServer().getPluginManager().registerEvents(new PluginListener(), this);
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		
        getServer().getPluginManager().registerEvents(new MagieListener(), this);
        getServer().getPluginManager().registerEvents(new TechListener(), this);
     	getServer().getPluginManager().registerEvents(new VieListener(), this);
     	getServer().getPluginManager().registerEvents(new MortListener(), this);
     	getServer().getPluginManager().registerEvents(new FeuListener(), this);
     	getServer().getPluginManager().registerEvents(new EauListener(), this);
     	getServer().getPluginManager().registerEvents(new TerreListener(), this);
     	getServer().getPluginManager().registerEvents(new AirListener(), this);
		
		System.out.println("[SkylandersFight] Plugin activé.");

		clearAllArmorStand();
		summonAllStatsArmorStand();
		
		TeamManager.deleteAll();
	}
	
	@Override
	public void onDisable() {
		System.out.println("[SkylandersFight] Plugin activé.");
	}
	
	private void clearAllArmorStand() {
		try {
			World w = Bukkit.getWorld("world");
			
			for (Entity entity : w.getEntities()) {
				if (entity instanceof ArmorStand) {
					ArmorStand as = (ArmorStand) entity;
					
					if (as.isInvisible() || as.isMarker()) {
						as.remove();
					}
				}
			}
			
			return;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage("§c[Error]§f (Plugin, clearAllArmorStand) : §7"+e.getMessage());
			return;
		}
	}
	
	private void summonAllStatsArmorStand() {
	    World world = Bukkit.getWorld("world");

	    int chunkRadius = 2; // Rayon de 2 chunks autour du centre

	    int centerChunkX = -898 >> 4;
	    int centerChunkZ = -480 >> 4;

	    for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
	        for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
	            Chunk chunk = world.getChunkAt(centerChunkX + dx, centerChunkZ + dz);
	            if (!chunk.isLoaded()) {
	                chunk.load();
	            }

	            for (Entity entity : chunk.getEntities()) {
	                if (entity instanceof ArmorStand) {
	                    Location loc = entity.getLocation();
	                    if (loc.distance(new Location(world, -898, loc.getY(), -480)) <= 15.0) {
	                        entity.remove();
	                    }
	                }
	            }
	        }
	    }
		
	    summonStatsArmorStand(this.statsUtils.getTopWinner(), "§7--- §6Seigneur de la Gloire§7 ---", -910.5, -480.5);
	    summonStatsArmorStand(this.statsUtils.getTopKDA(), "§7--- §6Maître du Destin§7 ---", -903.5, -481.5);
	    summonStatsArmorStand(this.statsUtils.getTopDamager(), "§7--- §6Lame du Chaos§7 ---", -896.5, -480.5);
	    summonStatsArmorStand(this.statsUtils.getTopHealer(), "§7--- §6Gardien de Vie§7 ---", -890.5, -480.5);
	    summonStatsArmorStand(this.statsUtils.getTopItemGetter(), "§7--- §6Chasseur de Reliques§7 ---", -885.5, -482.5);
	}
	
	private void summonStatsArmorStand(List<String> list, String title, Double x, Double z) {
	    World world = Bukkit.getWorld("world");

	    Double baseY = 62.5;
	    Double offsetY = 0.25;

	    Location titleLoc = new Location(world, x, baseY + (offsetY * list.size()), z);
	    ArmorStand titleAs = (ArmorStand) world.spawnEntity(titleLoc, EntityType.ARMOR_STAND);
	    titleAs.setInvisible(true);
	    titleAs.setInvulnerable(true);
	    titleAs.setCustomNameVisible(true);
	    titleAs.setCustomName(title);
	    titleAs.setGravity(false);
	    titleAs.setMarker(true);

	    for (int i = 0; i < list.size(); i++) {
	        String line = list.get(i);
	        Location loc = new Location(world, x, baseY + (offsetY * (list.size() - 1 - i)), z);

	        String prefix = "";
	        switch (i) {
	            case 0: prefix = "§6★ "; break;
	            case 1: prefix = "§7★ "; break;
	        }
	        
	        ArmorStand as = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
	        as.setInvisible(true);
	        as.setInvulnerable(true);
	        as.setCustomNameVisible(true);
	        as.setCustomName(prefix + line);
	        as.setGravity(false); 
	        as.setMarker(true);
	    }
	}

}
