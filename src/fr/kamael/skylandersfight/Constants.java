package fr.kamael.skylandersfight;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Constants {
	public final static String prefixError = "§c/!\\ Erreur /!\\ §f";
	public final static String prefixMessage = "§8[§6§lSkylanders§8] §f";
	public final static List<String> welcomeMessage = Arrays.asList(
			"vient d'être invoquer par le portail magique !", 
			"vient d'arriver pour casser la gueule de Kaos !",
			"vient de rejoindre les Skylands !");
	public final static List<String> goodbyeMessage = Arrays.asList(
			"vient de partir, enfin il était temps...", 
			"vient de se barrer, bon débarras !",
			"vient de s'enfuir, encore un lâche.");
	
	public final static String inventorySkylanderName = "§cSkylanders";
	public final static String inventoryArenaName = "§cArènes";
	public final static String inventoryConfigName = "§cConfiguration";
	
	public final static String itemNameEon = "§3Bénédiction d'Eon";
	public final static String itemNameFlynn = "§6Montgolfière de Flynn";
	public final static String itemNameHugo = "§2Savoir d'Hugo";
	public final static String itemNameCali = "§4Entraînement de Cali";
	public final static String itemNameKaos = "§8Malédiction de Kaos";
	public final static String itemNameGlumshank = "§9Affaiblissement de Glumshanks";
	
	public final static Location spawnLocation = new Location(Bukkit.getWorld("world"), -920, 62 , -483);
	public final static Double valueHeal = 5.;

}
