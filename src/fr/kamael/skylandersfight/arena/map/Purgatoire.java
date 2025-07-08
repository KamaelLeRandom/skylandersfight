package fr.kamael.skylandersfight.arena.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;

public class Purgatoire extends Arena {
	public static final String nameArena = "§4Le Purgatoire";
	public static final String nameEvent = "§4Vent Ardent";
	public static final Integer timerEvent = 60;

	public Purgatoire() {
		World w = Bukkit.getWorld("world");
		
		this.name = nameArena;
		
		this.playerSpawns.add(new Location(w, -1845, 23, 345));
		this.playerSpawns.add(new Location(w, -1845, 23, 284));
		this.playerSpawns.add(new Location(w, -1883, 22, 341));
		this.playerSpawns.add(new Location(w, -1839, 21, 383));
		this.playerSpawns.add(new Location(w, -1804, 26, 343));
		this.playerSpawns.add(new Location(w, -1846, 14, 331));
		
		this.healSpawns.add(new Location(w, -1837, 27, 319));
		this.healSpawns.add(new Location(w, -1850, 25, 287));
		this.healSpawns.add(new Location(w, -1883, 16, 350));
		this.healSpawns.add(new Location(w, -1849, 18, 347));
		this.healSpawns.add(new Location(w, -1795, 25, 336));
		this.healSpawns.add(new Location(w, -1817, 22, 306));
		this.healSpawns.add(new Location(w, -1845, 22, 352));
		
		this.itemSpawns.add(new Location(w, -1845, 23, 345));
		this.itemSpawns.add(new Location(w, -1845, 23, 284));
		this.itemSpawns.add(new Location(w, -1883, 22, 341));
		this.itemSpawns.add(new Location(w, -1839, 21, 383));
		this.itemSpawns.add(new Location(w, -1804, 26, 343));
		this.itemSpawns.add(new Location(w, -1846, 14, 331));
		
		this.elements.add(Element.FEU);
		this.elements.add(Element.AIR);
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
	public void onStart() {
		// TODO - Musique d'ambiance.
		Bukkit.getWorld("world").setTime(14000);
	}
	
	@Override
	public void event() {
		Bukkit.broadcastMessage(Constants.prefixMessage + "§e" + nameEvent + " §fest activé ! Une fois toutes les §b" + timerEvent + "§f secondes, le joueur avec le plus de §apoint de vie§f subira des dégats équivalent à la moitié de sa vie actuelle.");

		new BukkitRunnable() {
			private Integer timer = 0;
			
			@Override
			public void run() {
				if (!plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				if (timer != 0 && timer % timerEvent == 0) {
					Player player = null;
					Double health = 0.;
					
					for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
						if (player == null || gamePlayer.getPlayer().getHealth() < health) {
							player = gamePlayer.getPlayer();
							health = gamePlayer.getPlayer().getHealth();
						}
					}
					
					player.sendTitle(nameEvent, "Vous avez subi "+ health/2 +" dégats.", 1, 40, 1);
					player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 2, 2);
					player.damage(health / 2);
				}
				
				timer++;
			}
		}.runTaskTimer(plugin, 0, 20);	
	}
	
	public static ItemStack getItem() {
		ArrayList<String> lore = new ArrayList<>(List.of(
			"§7Bonus Élémentaire : " + Element.FEU.getName() + " §7| " + Element.AIR.getName(),
			"§fToutes les §b"+ timerEvent +"§f secondes, le "+ nameEvent +"§f souffle et §cinflige des dégats§f équivalent à la moitié de la vie actuelle du §ejoueur avec le plus de vie§f."
		));

		ItemStack item = new ItemStack(Material.NETHERRACK, 1);
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
