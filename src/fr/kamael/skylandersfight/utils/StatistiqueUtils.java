package fr.kamael.skylandersfight.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameStats;

public class StatistiqueUtils {
	private Plugin plugin = Plugin.plugin;
	private JSONParser parser;
	private JSONObject data;
	
	public StatistiqueUtils() {
		parser = new JSONParser();
		
		try {
			this.data = (JSONObject) parser.parse(new FileReader("skylanders-stats.json"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData() {
		try {
			FileWriter file = new FileWriter("skylanders-stats.json");
	        file.write(data.toString()); 
	        file.flush();
	        file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateDataAfterRound() {
		try {
			this.data = (JSONObject) parser.parse(new FileReader("skylanders-stats.json"));
			
			for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
				UUID uuid = gamePlayer.getPlayer().getUniqueId();
				GameStats stats = gamePlayer.getStats();
				JSONObject dataPlayer = (JSONObject) data.get(uuid.toString());
				
				if (gamePlayer.getSkylander().isAlive()) {
					dataPlayer.put("nbRoundWin", (Long) dataPlayer.get("nbRoundWin") + 1);
				}
				
				dataPlayer.put("nbRound", (Long) dataPlayer.get("nbRound") + 1);
				dataPlayer.put("nbKill", (Long) dataPlayer.get("nbKill") + stats.nbKill);
				dataPlayer.put("nbDeath", (Long) dataPlayer.get("nbDeath") + stats.nbDeath);
				dataPlayer.put("nbAssist", (Long) dataPlayer.get("nbAssist") + stats.nbAssist);
				dataPlayer.put("nbDamage", (Double) dataPlayer.get("nbDamage") + stats.nbDamage);
				dataPlayer.put("nbItem", (Long) dataPlayer.get("nbItem") + stats.nbItem);
				dataPlayer.put("nbHeal", (Long) dataPlayer.get("nbHeal") + stats.nbHeal);
				
				this.data.replace(uuid, dataPlayer);
			}
			
			writeData();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void tryInitializaJSONPlayer(Player player) {
		try {
			JSONObject stats = new JSONObject();
			stats.put("nbRound", 0);
			stats.put("nbRoundWin", 0);
			stats.put("nbKill", 0);
			stats.put("nbDeath", 0);
			stats.put("nbAssist", 0);
			stats.put("nbDamage", 0.);
			stats.put("nbItem", 0);
			stats.put("nbHeal", 0);
			
			this.data.putIfAbsent(player.getUniqueId().toString(), stats);
			
			writeData();
        } catch (Exception e) {
        	e.printStackTrace();
		}
	}
}
