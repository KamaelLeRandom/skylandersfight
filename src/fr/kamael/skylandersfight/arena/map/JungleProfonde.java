package fr.kamael.skylandersfight.arena.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;

public class JungleProfonde extends Arena {
	public static final String nameArena = "§2Jungle Profonde";
	public static final String nameEvent = "§2Sens de l'Orientation";
	public static final Integer numberTeleportEvent = 2;

	public JungleProfonde() {
	    World w = Bukkit.getWorld("world");
	    
	    this.name = "§2Jungle Profonde";
	    
	    this.playerSpawns.add(new Location(w, -1847, 48, -102));
	    this.playerSpawns.add(new Location(w, -1915, 52, -149));
	    this.playerSpawns.add(new Location(w, -1842, 47, -150));
	    this.playerSpawns.add(new Location(w, -1808, 85, -155));
	    this.playerSpawns.add(new Location(w, -1808, 85, -47));
	    this.playerSpawns.add(new Location(w, -1912, 48, -49));
	    this.playerSpawns.add(new Location(w, -1912, 47, -128));
	    this.playerSpawns.add(new Location(w, -1808, 66, -92));
	    this.playerSpawns.add(new Location(w, -1805, 49, -70));
	    this.playerSpawns.add(new Location(w, -1772, 58, -131));
	    this.playerSpawns.add(new Location(w, -1740, 87, -153));
	    this.playerSpawns.add(new Location(w, -1745, 90, -132));
	    this.playerSpawns.add(new Location(w, -1736, 86, -89));
	    this.playerSpawns.add(new Location(w, -1734, 86, -47));
	    this.playerSpawns.add(new Location(w, -1737, 122, -84));
	    this.playerSpawns.add(new Location(w, -1764, 86, -105));
	    
	    this.healSpawns.add(new Location(w, -1800, 50, -67));
	    this.healSpawns.add(new Location(w, -1820, 57, -50));
	    this.healSpawns.add(new Location(w, -1813, 64, -93));
	    this.healSpawns.add(new Location(w, -1815, 59, -139));
	    this.healSpawns.add(new Location(w, -1846, 54, -105));
	    this.healSpawns.add(new Location(w, -1902, 54 , -53));
	    this.healSpawns.add(new Location(w, -1917, 46, -122));
	    this.healSpawns.add(new Location(w, -1918, 45, -146));
	    this.healSpawns.add(new Location(w, -1803, 84, -107));
	    this.healSpawns.add(new Location(w, -1773, 56, -132));
	    this.healSpawns.add(new Location(w, -1745, 92, -135));
	    this.healSpawns.add(new Location(w, -1756, 85, -155));
	    this.healSpawns.add(new Location(w, -1743, 85, -64));
	    this.healSpawns.add(new Location(w, -1760, 111, -66));
	    this.healSpawns.add(new Location(w, -1747, 115, -80));
	    
	    this.itemSpawns.add(new Location(w, -1905.5, 46, -146.5));
	    this.itemSpawns.add(new Location(w, -1920.5, 54, -61.5));
	    this.itemSpawns.add(new Location(w, -1816.5, 67, -52.5));
	    this.itemSpawns.add(new Location(w, -1821.5, 65, -158.5));
	    this.itemSpawns.add(new Location(w, -1807.5, 80, -90.5));
	    this.itemSpawns.add(new Location(w, -1732.5, 47, -92.5));
	    this.itemSpawns.add(new Location(w, -1742.5, 85, -92.5));
	    this.itemSpawns.add(new Location(w, -1762.5, 85, -50.5));
	    this.itemSpawns.add(new Location(w, -1743.5, 115, -78.5));
	    this.itemSpawns.add(new Location(w, -1747.5, 85, -132.5));
	    this.itemSpawns.add(new Location(w, -1762.5, 63, -149.5));
	    this.itemSpawns.add(new Location(w, -1802.5, 50, -75.5));
	    
	    this.elements.add(Element.VIE);
	    this.elements.add(Element.EAU);
	}

	@Override
	public void deathmatch() {
		deathmatch = true;
		this.playerSpawns.removeAll(this.playerSpawns);
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1500.5, 3.5, 105.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1490.5, 3.5, 115.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1501.5, 3.5, 117.5));
		this.playerSpawns.add(new Location(Bukkit.getWorld("world"), -1483.5, 3.5, 100.5));
		return;
	}
	
	@Override
	public void event() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(1000);
		
		if (plugin.game.getConfig().getActiveEventMap() && plugin.game.getPlayers().size() >= numberTeleportEvent) {
			Bukkit.broadcastMessage(Constants.prefixMessage + "L'événement "+ nameEvent +"§f est activé, à chaque minute il y a 50% de chance que "+ numberTeleportEvent +" joueurs choisit aléatoirement soient téléporté au même endroit");
			
			new BukkitRunnable() {
				private Integer timer = 0;
				private ArrayList<GamePlayer> players = plugin.game.getPlayers();
				@Override
				public void run() {
					if (!plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					if (timer%60 == 0 && timer != 0 && plugin.random.nextBoolean()) {
						ArrayList<Skylander> skylanderTeleport = new ArrayList<Skylander>();
						
						Location location = getRandomItemSpawn();
						Integer attempts = 0;
						
						while (skylanderTeleport.size() != numberTeleportEvent) {

							Skylander skylanderCheck = players.get(plugin.random.nextInt(players.size())).getSkylander();
							
							if (skylanderCheck.isAlive() && !skylanderCheck.checkStatus(Status.NOTELEPORT) && !skylanderTeleport.contains(skylanderCheck)) {
								skylanderTeleport.add(skylanderCheck);
							}
							
		                    if (++attempts > 30) {
		                        return;
		                    }
						}
						
						for (Skylander skylander : skylanderTeleport) {
							skylander.getPlayer().sendTitle(nameEvent, "Vous allez être téléporté dans 2s.", 1, 40, 1);
						}
						
						new BukkitRunnable() {
							
							@Override
							public void run() {
								for (Skylander skylander : skylanderTeleport) {
									skylander.getPlayer().teleport(location);
								}
								cancel();
								return;
							}
						}.runTaskLater(plugin, 40);
					}
					
					timer++;
				}
			}.runTaskTimer(plugin, 0, 20);
		}
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of("§fBonus Élémentaire : §7"+ Element.VIE.getName() + " | " + Element.EAU.getName()));
		
		ItemStack item = new ItemStack(Material.JUNGLE_LOG, 1);
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
