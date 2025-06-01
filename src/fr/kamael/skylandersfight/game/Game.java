package fr.kamael.skylandersfight.game;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.config.Config;
import fr.kamael.skylandersfight.game.config.ConfigSkylander;
import fr.kamael.skylandersfight.utils.manager.FireworkManager;
import fr.kamael.skylandersfight.utils.manager.ItemManager;
import fr.kamael.skylandersfight.utils.manager.TeamManager;

public class Game {
	private Plugin plugin = Plugin.plugin;
	private Config config;
	private ConfigSkylander configSkylander;
	private GameState state;
	private GameRound round;
	private Integer numeroRound;
	private ArrayList<GamePlayer> listPlayers;
	private ArrayList<GameTeam> listTeams;
	private Team teamDead;
	
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
	
	public Team getTeamDead() {
		return this.teamDead;
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
			player.closeInventory();
			plugin.playerUtils.unnickAllPlayer();
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
		    
		    StringBuilder msgKaos = new StringBuilder("§8========= §c§lÉquipe KAOS §8=========\n");
		    StringBuilder msgEon = new StringBuilder("§8========= §b§lÉquipe EON §8=========\n");

		    for (GamePlayer gamePlayer : team1) {
		        msgKaos.append("§7→ §f").append(gamePlayer.getPlayer().getName()).append("\n");
		    }

		    for (GamePlayer gamePlayer : team2) {
		        msgEon.append("§7→ §f").append(gamePlayer.getPlayer().getName()).append("\n");
		    }

		    msgKaos.append("§8====================\n");
		    msgEon.append("§8====================\n");

		    Bukkit.broadcastMessage(msgKaos.toString());
		    Bukkit.broadcastMessage(msgEon.toString());

		    this.listTeams.add(new GameTeam("Kaos", team1, ChatColor.RED));
		    this.listTeams.add(new GameTeam("Eon", team2, ChatColor.AQUA));
		} 
		// Mode : Solo
		else {
			Integer idxColor = 0;
			
			for (GamePlayer gamePlayer : listPlayers) {
				ChatColor color = TeamManager.COLORS.get(idxColor);
				this.listTeams.add(new GameTeam(gamePlayer.getPlayer().getName(), gamePlayer, color));
				idxColor++;
			}
		}
		
		this.teamDead = TeamManager.create("Dead", ChatColor.BLACK);
		
		ArrayList<String> list = new ArrayList<>();
		list.add("§fPoints : §6"+ config.getNbPointWin() +"§f points gagnant");
		list.add("§fÉquipes " + (config.getActiveTeam() ? "§aactivé" : "§cdésactivé"));
		list.add("§fDeathmatch " + (config.getActiveDeathmatch() ? "§aactivé§f (§7"+ config.getTimerDM() +"min§f)" : "§cdésactivé"));
		list.add("§fObjets " + (config.getActiveItem() ? "§aactivé" : "§cdésactivé"));
		list.add("§fBlocs de soin " + (config.getActiveHeal() ? "§aactivé" : "§cdésactivé"));
		
		new BukkitRunnable() {
			private Integer compteur = 0;
			
			@Override
			public void run() {
				if (compteur+1 > list.size()) {
					playRound();
					cancel();
					return;
				}
				
				for (GamePlayer gamePlayer : listPlayers) {
					gamePlayer.getPlayer().sendTitle("§6Skylanders-Fight§f", list.get(compteur), 5, 20, 5);
				}
				
				compteur++;
			}
		}.runTaskTimer(plugin, 0, 30);
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
