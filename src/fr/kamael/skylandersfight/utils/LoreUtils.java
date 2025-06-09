package fr.kamael.skylandersfight.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.kamael.skylandersfight.Plugin;

public class LoreUtils {
    private static final File file = new File("skylanders-lore.json");
    private static final File folder = new File("plugins/Skylanders/Chapters");
	private JSONParser parser;
	private JSONObject data;
	
    public LoreUtils() {
    	parser = new JSONParser();
        loadData();
    }
    
    @SuppressWarnings("unchecked")
	public void initPlayerIfNeeded(UUID uuid) {
        String uuidStr = uuid.toString();
        if (!data.containsKey(uuidStr)) {
            data.put(uuidStr, new JSONArray());
            saveData();
        }
    }

    private void loadData() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                data = new JSONObject();
                saveData();
            } else {
                FileReader reader = new FileReader(file);
                data = (JSONObject) parser.parse(reader);
                reader.close();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            data = new JSONObject();
        }
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getChapterDiscoveredPlayer(UUID uuid) {
        JSONArray chapters = (JSONArray) data.get(uuid.toString());
        if (chapters == null) return new ArrayList<>();
        
        ArrayList<Integer> result = new ArrayList<>();
        for (Object o : chapters) {
            result.add(((Long) o).intValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	public void updateChapterDiscoveredPlayer(UUID uuid, Integer chapter) {
        String uuidStr = uuid.toString();
        JSONArray chapters = (JSONArray) data.get(uuidStr);
        if (chapters == null) chapters = new JSONArray();

        if (!chapters.contains(chapter)) {
            chapters.add(chapter);
            data.put(uuidStr, chapters);
            saveData();
        }
    }
    
    public List<String> getChapterPages(int chapterId) {
        File file = new File(folder, "chapter-" + chapterId + ".txt");
        if (!file.exists()) return List.of("§cChapitre introuvable.");

        List<String> pages = new ArrayList<String>();
        StringBuilder pageBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("===PAGE===")) {
                    pages.add(pageBuilder.toString().trim());
                    pageBuilder = new StringBuilder();
                } else {
                    pageBuilder.append(line).append("\n");
                }
            }

            if (pageBuilder.length() > 0) {
                pages.add(pageBuilder.toString().trim());
            }

        } catch (IOException e) {
            pages.add("§cErreur de lecture.");
            e.printStackTrace();
        }

        return pages;
    }
    
    public void openBook(Player player, ItemStack book) {
        Integer slot = player.getInventory().getHeldItemSlot();
        ItemStack oldItem = player.getInventory().getItem(slot);

        player.getInventory().setItem(slot, book);

        ((CraftPlayer) player).getHandle().openBook(CraftItemStack.asNMSCopy(book), net.minecraft.world.EnumHand.a);

        Bukkit.getScheduler().runTaskLater(Plugin.plugin, () -> {
            player.getInventory().setItem(slot, oldItem);
        }, 10L);
    }
}
