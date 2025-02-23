package fr.kamael.skylandersfight;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kamael.skylandersfight.arena.ArenaListener;
import fr.kamael.skylandersfight.command.SkylanderCommand;
import fr.kamael.skylandersfight.command.SkylanderTabCompleter;
import fr.kamael.skylandersfight.game.Game;
import fr.kamael.skylandersfight.game.GameListener;
import fr.kamael.skylandersfight.skylanders.magie.listener.MagieListener;

public class Plugin extends JavaPlugin {
	public static Plugin plugin;
	public Random random;
	public Game game;
	
	/// --- Méthodes initial.
	@Override
	public void onEnable() {
		plugin = this;
		random = new Random();

        getCommand("skylander").setExecutor(new SkylanderCommand());
        getCommand("skylander").setTabCompleter(new SkylanderTabCompleter());
		
		getServer().getPluginManager().registerEvents(new PluginListener(), this);
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		
		getServer().getPluginManager().registerEvents(new MagieListener(), this);
		
		System.out.println("[SkylandersFight] Plugin activé.");
		
		clearAllArmorStand();
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
}
