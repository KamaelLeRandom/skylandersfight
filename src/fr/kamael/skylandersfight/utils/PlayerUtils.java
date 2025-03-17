package fr.kamael.skylandersfight.utils;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.kamael.skylandersfight.Plugin;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;

public class PlayerUtils {
	private Plugin plugin = Plugin.plugin;
	private HashMap<Player, String> nicked = new HashMap<Player, String>();
	
    public Boolean isNicked(Player player) {
        if (nicked.containsKey(player)) {
            return true;
        }
        return false;
    }
	
    public void nickPlayer(Player player, String name) {
        if (!isNicked(player)) {
            String s = player.getName();
            try {
                GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
                Field ff = playerProfile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(playerProfile, name);
                player.setPlayerListName(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            nicked.put(player, s);
            
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(plugin, player);
                pl.showPlayer(plugin, player);
            }

            changeSkin(player, name);
        }
    }

    public void unnickPlayer(Player player) {
        if (isNicked(player)) {
            String name = nicked.get(player);
            
            try {
                GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
                Field ff = playerProfile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(playerProfile, name);
                player.setPlayerListName(player.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            nicked.remove(player);
            
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(plugin, player);
                pl.showPlayer(plugin, player);
            }

            changeSkin(player, name);
        }
    }
    
    public static void changeSkin(Player player, String skinname) {
        if (player == null) return;

        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        GameProfile gameProfile = entityPlayer.getProfile();

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p != player)
                .forEach(onlinePlayer -> {
                    CraftPlayer onlineCraftPlayer = (CraftPlayer) onlinePlayer;
                    PlayerConnection onlineConnection = onlineCraftPlayer.getHandle().b;

                    // Supprime le joueur pour les autres joueurs
                    onlineConnection.sendPacket(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d, // REMOVE_PLAYER en 1.17
                            entityPlayer
                    ));

                    // Mise à jour des textures
                    gameProfile.getProperties().removeAll("textures");
                    gameProfile.getProperties().put("textures", getTexturesProperty(skinname));

                    // Réajoute le joueur avec la nouvelle skin
                    onlineConnection.sendPacket(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, // ADD_PLAYER en 1.17
                            entityPlayer
                    ));

                    int entityId = entityPlayer.getId();
                    
                    onlineConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
                    onlineConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
                });
    }

    public static Property getTexturesProperty(String playerName) {
        try {
            // 1. Récupérer l'UUID du joueur
            String uuid = getUUIDFromName(playerName);
            if (uuid == null) {
                System.out.println("⚠️ Impossible de récupérer l'UUID pour le joueur: " + playerName);
                return null;
            }

            // 2. Récupérer les textures à partir de l'UUID
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                System.out.println("⚠️ Erreur Mojang API: Code " + connection.getResponseCode());
                return null;
            }

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

            if (!json.has("properties") || json.getAsJsonArray("properties").size() == 0) {
                System.out.println("⚠️ Aucune propriété trouvée pour " + playerName);
                return null;
            }

            // 3. Extraire la texture et sa signature
            JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            String texture = properties.get("value").getAsString();
            String signature = properties.get("signature").getAsString();

            return new Property("textures", texture, signature);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUUIDFromName(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("⚠️ Erreur Mojang API: Code " + responseCode);
                return null;
            }

            // Lire la réponse correctement
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject json =  new JsonParser().parse(reader).getAsJsonObject();
            reader.close();

            if (!json.has("id")) {
                System.out.println("⚠️ Aucun UUID trouvé pour " + playerName);
                return null;
            }

            return json.get("id").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
