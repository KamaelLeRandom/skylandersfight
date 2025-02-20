package fr.kamael.skylandersfight;

import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;

import fr.kamael.skylandersfight.arena.ArenaListener;
import fr.kamael.skylandersfight.game.Game;

public class Plugin extends JavaPlugin {
	public static Plugin plugin;
	public Random random;
	public Game game;
	
	/// --- Méthodes initial.
	@Override
	public void onEnable() {
		plugin = this;
		random = new Random();

		getServer().getPluginManager().registerEvents(new ArenaListener(), this);
		
		System.out.println("[SkylandersFight] Plugin activé.");
	}
	
	@Override
	public void onDisable() {
		System.out.println("[SkylandersFight] Plugin activé.");
	}
}
