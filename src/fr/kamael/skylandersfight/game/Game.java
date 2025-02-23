package fr.kamael.skylandersfight.game;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.config.Config;
import fr.kamael.skylandersfight.game.config.ConfigSkylander;
import fr.kamael.skylandersfight.utils.manager.FireworkManager;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Game {
	private Plugin plugin = Plugin.plugin;
	private Config config;
	private ConfigSkylander configSkylander;
	private GameState state;
	private GameRound round;
	private Integer numeroRound;
	private ArrayList<GamePlayer> listPlayers;
	private ArrayList<GameTeam> listTeams;
	
	public Game() {
		this.config = new Config();
		this.configSkylander = new ConfigSkylander();
		this.state = GameState.WAITING;
		this.numeroRound = 0;
		this.listTeams = new ArrayList<GameTeam>();
		this.listPlayers = new ArrayList<GamePlayer>();
	}
	
	public Config getConfig() {
		return this.config;
	}
	
	public ConfigSkylander getConfigSkylander() {
		return this.configSkylander;
	}
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public Boolean isState(GameState state) {
		if (this.state.equals(state)) {
			return true;
		}
		return false;
	}
	
	public GameRound getRound() {
		return this.round;
	}
	
	public GamePlayer getPlayer(Player player) {
		for (GamePlayer gamePlayer : this.listPlayers) {
			if (gamePlayer.getPlayer().equals(player)) {
				return gamePlayer;
			}
		}
		return null;
	}
	
	public ArrayList<GamePlayer> getPlayers() {
		return this.listPlayers;
	}
	
	public ArrayList<GameTeam> getTeams() {
		return this.listTeams;
	}
	
	public void start() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.listPlayers.add(new GamePlayer(player));
		}
		
		// Mode : Equipe
		if (config.getActiveTeam()) {
			ArrayList<GamePlayer> shuffledPlayers = new ArrayList<>(listPlayers);
		    Collections.shuffle(shuffledPlayers);
		    
		    ArrayList<GamePlayer> team1 = new ArrayList<>();
		    ArrayList<GamePlayer> team2 = new ArrayList<>();

		    for (int i = 0; i < shuffledPlayers.size(); i++) {
		        if (i % 2 == 0) {
		            team1.add(shuffledPlayers.get(i));
		        } else {
		            team2.add(shuffledPlayers.get(i));
		        }
		    }

		    this.listTeams.add(new GameTeam("§cKaos", team1));
		    this.listTeams.add(new GameTeam("§bEon", team2));
		} 
		// Mode : Solo
		else {
			for (GamePlayer gamePlayer : listPlayers) {
				this.listTeams.add(new GameTeam("§7"+gamePlayer.getPlayer().getName(), gamePlayer));
			}
		}
		
		playRound();
	}
	
	public void playRound() {
		numeroRound++;
		round = new GameRound(numeroRound);
	}
	
	public void checkVictory() {
	    StringBuilder resume = new StringBuilder("§6§l===== Récapitulatif des Scores =====\n");

	    for (GameTeam gameTeam : listTeams) {
	        if (gameTeam.getNbPoint().equals(this.config.getNbPointWin())) {
	            finish(gameTeam);
	            return;
	        } else {
	            resume.append("§e→ §bÉquipe §6").append(gameTeam.getName())
	                  .append("§f : §a").append(gameTeam.getNbPoint()).append(" §epoints\n");
	        }
	    }

	    resume.append("§6=================================");
	    
	    Bukkit.broadcastMessage(resume.toString());
	    
	    playRound();
	}
	
	@SuppressWarnings("deprecation")
	public void finish(GameTeam winningTeam) {
		this.state = GameState.ENDING;
		
		Bukkit.broadcastMessage(Constants.prefixMessage + "Félicitation à l'équipe "+ winningTeam.getName() +"§f d'avoir remporté la partie avec un total de §c"+ winningTeam.getNbKill() +"§f éliminations !");
		
		for (GamePlayer gamePlayer : listPlayers) {
			Player player = gamePlayer.getPlayer();
			player.teleport(Constants.spawnLocation);
			player.setGameMode(GameMode.ADVENTURE);
			ItemManager.clearPlayer(player);
			
			if (winningTeam.getPlayers().contains(gamePlayer)) {
				player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
				player.setPlayerListName("§e★ " + player.getName());
				player.sendTitle("§aVictoire !", "§7Juste le GOAT enfaite");
			} else {
				player.setPlayerListName(player.getName());
				player.sendTitle("§cDéfaite !", "§7Bah frérot tu pues la merde");
			}
		}
		
		new BukkitRunnable() {
			private Integer timer = 5;
			@Override
			public void run() {
				// Condition d'arrêt.
				if (timer == 0) {
					cancel();
					return;
				}
				
				for (GamePlayer gamePlayer : winningTeam.getPlayers()) {					
					FireworkManager.launchVictoryFirework(gamePlayer.getPlayer().getLocation());
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
