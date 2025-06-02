package fr.kamael.skylandersfight.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
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
	
	public List<String> getTopKDA() {
	    List<Map.Entry<String, Double>> kdaList = new ArrayList<>();

	    for (Object key : data.keySet()) {
	        String uuidStr = (String) key;
	        JSONObject stats = (JSONObject) data.get(uuidStr);

	        if (stats != null && stats.containsKey("nbKill") && stats.containsKey("nbAssist") && stats.containsKey("nbDeath")) {
	            long kills = (Long) stats.get("nbKill");
	            long assists = (Long) stats.get("nbAssist");
	            long deaths = (Long) stats.get("nbDeath");

	            double kda = (double) (kills + assists * 0.5) / Math.max(1, deaths);
	            kdaList.add(new AbstractMap.SimpleEntry<>(uuidStr, kda));
	        }
	    }

	    List<Map.Entry<String, Double>> sorted = kdaList.stream()
	        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
	        .limit(10)
	        .collect(Collectors.toList());

	    List<String> result = new ArrayList<>();
	    for (Map.Entry<String, Double> entry : sorted) {
	        String uuidStr = entry.getKey();
	        UUID uuid = UUID.fromString(uuidStr);
	        String name = Bukkit.getOfflinePlayer(uuid).getName();
	        result.add(String.format("§6%s §f: §c%.2f§f KDA", name, entry.getValue()));
	    }

	    return result;
	}
	
	public List<String> getTopDamager() {
	    List<Map.Entry<String, Double>> damageList = new ArrayList<>();

	    for (Object key : data.keySet()) {
	        String uuidStr = (String) key;
	        JSONObject stats = (JSONObject) data.get(uuidStr);

	        if (stats != null && stats.containsKey("nbDamage") && stats.containsKey("nbRound")) {
	            double totalDamage = (Double) stats.get("nbDamage");
	            long nbRound = (Long) stats.get("nbRound");

	            if (nbRound > 0) {
	                double average = totalDamage / nbRound;
	                damageList.add(new AbstractMap.SimpleEntry<>(uuidStr, average));
	            }
	        }
	    }

	    List<Map.Entry<String, Double>> sorted = damageList.stream()
	        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
	        .collect(Collectors.toList());

	    Collections.reverse(sorted);
	    sorted = sorted.stream().limit(10).collect(Collectors.toList());

	    List<String> result = new ArrayList<>();
	    for (Map.Entry<String, Double> entry : sorted) {
	        String uuidStr = entry.getKey();
	        UUID uuid = UUID.fromString(uuidStr);
	        String name = Bukkit.getOfflinePlayer(uuid).getName();

	        JSONObject stats = (JSONObject) data.get(uuidStr);
	        double totalDamage = (Double) stats.get("nbDamage");

	        result.add(String.format("§6%s§f : §c%.1f§f dégâts/game (§c%.1f§f au total)", name, entry.getValue(), totalDamage));
	    }

	    return result;
	}
	
	public List<String> getTopHealer() {
	    List<Map.Entry<String, Long>> healList = new ArrayList<>();

	    for (Object key : data.keySet()) {
	        String uuidStr = (String) key;
	        JSONObject stats = (JSONObject) data.get(uuidStr);
	        if (stats != null && stats.containsKey("nbHeal")) {
	            Long nbHeal = (Long) stats.get("nbHeal");
	            healList.add(new AbstractMap.SimpleEntry<>(uuidStr, nbHeal));
	        }
	    }

	    List<Map.Entry<String, Long>> sorted = healList.stream()
	        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
	        .limit(10)
	        .collect(Collectors.toList());

	    List<String> result = new ArrayList<>();
	    for (Map.Entry<String, Long> entry : sorted) {
	        String uuidStr = entry.getKey();
	        UUID uuid = UUID.fromString(uuidStr);
	        String name = Bukkit.getOfflinePlayer(uuid).getName();
	        result.add(name + " : " + entry.getValue());
	    }

	    return result;
	}
	
	public List<String> getTopWinner() {
	    List<Map.Entry<String, Double>> ratioList = new ArrayList<>();

	    for (Object key : data.keySet()) {
	        String uuidStr = (String) key;
	        JSONObject stats = (JSONObject) data.get(uuidStr);
	        
	        if (stats != null && stats.containsKey("nbRound") && stats.containsKey("nbRoundWin")) {
	            long nbRound = (Long) stats.get("nbRound");
	            long nbRoundWin = (Long) stats.get("nbRoundWin");

	            if (nbRound > 0) {
	                double ratio = (double) nbRoundWin / nbRound;
	                ratioList.add(new AbstractMap.SimpleEntry<>(uuidStr, ratio));
	            }
	        }
	    }

	    List<Map.Entry<String, Double>> sorted = ratioList.stream()
	        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
	        .limit(10)
	        .collect(Collectors.toList());

	    List<String> result = new ArrayList<>();
	    for (Map.Entry<String, Double> entry : sorted) {
	        String uuidStr = entry.getKey();
	        UUID uuid = UUID.fromString(uuidStr);
	        String name = Bukkit.getOfflinePlayer(uuid).getName();
	        double ratio = entry.getValue() * 100;
	        result.add(String.format("%s : %.2f%% de victoires", name, ratio));
	    }

	    return result;
	}
	
	public List<String> getTopItemGetter() {
	    List<Map.Entry<String, Long>> itemList = new ArrayList<>();

	    for (Object key : data.keySet()) {
	        String uuidStr = (String) key;
	        JSONObject stats = (JSONObject) data.get(uuidStr);
	        
	        if (stats != null && stats.containsKey("nbItem")) {
	            long nbItem = (Long) stats.get("nbItem");
	            itemList.add(new AbstractMap.SimpleEntry<>(uuidStr, nbItem));
	        }
	    }

	    List<Map.Entry<String, Long>> sorted = itemList.stream()
	        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
	        .limit(10)
	        .collect(Collectors.toList());

	    List<String> result = new ArrayList<>();
	    for (Map.Entry<String, Long> entry : sorted) {
	        String uuidStr = entry.getKey();
	        UUID uuid = UUID.fromString(uuidStr);
	        String name = Bukkit.getOfflinePlayer(uuid).getName();
	        result.add(String.format("§6%s§f : §c%d§f objets récupérés", name, entry.getValue()));
	    }

	    return result;
	}
}
