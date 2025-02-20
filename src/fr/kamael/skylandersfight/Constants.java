package fr.kamael.skylandersfight;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Constants {
	public static String prefixError = "§c/!\\ Erreur /!\\ §f";
	public static String prefixMessage = "§8[§6§lSkylanders§8] §f";
	public static List<String> welcomeMessage = Arrays.asList(
			"vient d'être invoquer par le portail magique !", 
			"vient d'arriver pour casser la gueule de Kaos !",
			"vient de rejoindre les Skylands !");
	public static List<String> goodbyeMessage = Arrays.asList(
			"vient de partir, enfin il était temps...", 
			"vient de se barrer, bon débarras !",
			"vient de s'enfuir, encore un lâche.");
	public static String inventoryConfigName = "§cConfiguration";
	public static String itemNameEon = "§3Bénédiction d'Eon";
	public static String itemNameFlynn = "§6Montgolfière de Flynn";
	public static String itemNameHugo = "§2Savoir d'Hugo";
	public static String itemNameCali = "§4Entraînement de Cali";
	public static String itemNameKaos = "§8Malédiction de Kaos";
	public static String itemNameGlumshank = "§9Affaiblissement de Glumshanks";
	
	public static Location spawnLocation = new Location(Bukkit.getWorld("world"), -920, 62 , -483);

}
