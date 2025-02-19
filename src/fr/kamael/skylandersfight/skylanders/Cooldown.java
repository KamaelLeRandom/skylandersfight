package fr.kamael.skylandersfight.skylanders;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Cooldown {
	private Plugin plugin = Plugin.plugin;
	private String name;
	private Integer timer;
	private Skylander skylander;
	
	public Cooldown(Skylander skylander, String name, Integer seconds) {
		this.name = name;
		this.timer = seconds;
		this.skylander = skylander;
		
		startCooldown();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void startCooldown() {
		new BukkitRunnable() {
			@Override
			public void run() {
				// Condition d'arrêt.
				if (timer == 0 || !plugin.game.isState(GameState.FIGHTING)) {
					endCooldown();
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void endCooldown() {
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous pouvez réutiliser le sort : "+ name +"§f.");
		skylander.removeCooldown(this);
	}
	
	public void printCooldown() {
		Player player = skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(name +"§7 est en récupération. (§c" + timer + "§7s)."));
	}
}
