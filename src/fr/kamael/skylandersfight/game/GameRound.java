package fr.kamael.skylandersfight.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.ArenaInventory;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.SkylanderInventory;
import fr.kamael.skylandersfight.utils.converter.ArenaConverter;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;

public class GameRound {
	private Plugin plugin = Plugin.plugin;
	private Arena arena;
	private Integer timerRound;
	
	public GameRound(Integer numeroRound) {
		this.timerRound = 0;
		this.plugin.game.setState(GameState.CHOOSING);
		
		chooseSkylander();
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public void chooseElement() {
		Element element = this.arena.getRandomElement();
		
		for (GamePlayer gamePlayer : this.plugin.game.getPlayers()) {
			Skylander skylander = gamePlayer.getSkylander();
			Player player = gamePlayer.getPlayer();
			player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1, 1);
			player.sendTitle("§8Bonus Élémentaire :", element.getName(), 5, 20, 5);
			player.sendMessage(Constants.prefixMessage + "L'Élement de l'Arène est : "+ element.getName() +".");
			
			if (skylander.getElement().equals(element)) {
				skylander.updateForce(+0.1);
				skylander.updateResis(-0.1);
			}
		}
	}
	
	public void chooseSkylander() { 
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
			gamePlayer.getPlayer().openInventory(SkylanderInventory.getRandomInventory());
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Boolean isValid = true;
				
				for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
					if (gamePlayer.getSkylander() == null) {
						isValid = false;
					}
				}
				
				if (isValid) {
					chooseArena();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void chooseArena() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
			gamePlayer.getPlayer().openInventory(ArenaInventory.getInventory());
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Boolean isValid = true;
				
				for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
					if (gamePlayer.getVotedArena() == null) {
						isValid = false;
					}
				}
				
				if (isValid) {
					start();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void start() {
		ArrayList<GamePlayer> listPlayers = this.plugin.game.getPlayers();
		
		this.arena = ArenaConverter.convert(listPlayers.get(plugin.random.nextInt(listPlayers.size())).getVotedArena());
		
		for (GamePlayer gamePlayer : listPlayers) {
			Player player = gamePlayer.getPlayer();
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
			player.sendTitle("§7Arène :", this.arena.getName(), 5, 55, 5);
		}
		
		new BukkitRunnable() {
			private Integer timer = 3;
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Constants.prefixMessage + "La manche démarre dans §b"+ timer +"§f secondes !");
				
				if (timer == 1) {
					play();
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void play() {
		this.plugin.game.setState(GameState.FIGHTING);
		
		for (GamePlayer gamePlayer : this.plugin.game.getPlayers()) {
			Skylander skylander = gamePlayer.getSkylander();
			
			gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
			gamePlayer.setActualTeam(gamePlayer.getInitialTeam());
			
			skylander.summonInfoArmorStand();
			skylander.giveEquipement();
			skylander.setFullHealth();
			skylander.onStart();
		}
		
		arena.teleportAllPlayer();
		arena.resetHeal();
		arena.event();
		
		new BukkitRunnable() {
			private ArrayList<GamePlayer> listPlayers = plugin.game.getPlayers();
			private Integer timerItem = 15 + (plugin.random.nextInt(45)+1);
			private Integer timer = 0;
			
			@Override
			public void run() {
				if (!plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				for (GamePlayer gamePlayer : listPlayers) {
					Skylander skylander = gamePlayer.getSkylander();
					
					if (skylander.isAlive()) {
						skylander.updateInfoArmorStand();
					}
				}
				
				if (timer%10 == 0) {
					timerRound++;
					timerItem--;
					
					updateScoreboard();
					
					if (timer == 5) {
						if (plugin.game.getConfig().getActiveBonusMap()) {
							chooseElement();
						}
					}
					
					if (timerItem == 0) {
						arena.summonArenaItem();
						timerItem = 15 + (plugin.random.nextInt(45)+1);
					}
				}
								
				timer++;
			}
		}.runTaskTimer(plugin, 0, 2);
	}
	
	public void checkVictory() {
		ArrayList<GameTeam> teamsAlive = new ArrayList<>();
		
		for (GamePlayer gamePlayer : this.plugin.game.getPlayers()) {
			if (gamePlayer.getSkylander().isAlive() && !teamsAlive.contains(gamePlayer.getActualTeam())) {
				teamsAlive.add(gamePlayer.getActualTeam());
			}
		}
		
		if (teamsAlive.size() == 1) {
			GameTeam gameTeam = teamsAlive.get(0);
			gameTeam.updateNbPoint(+1);
			finish(gameTeam);
		}
	}
	
	public void finish(GameTeam gameTeam) {
		this.plugin.game.setState(GameState.WAITING);
		this.arena.removeAllCustomEntities();
		this.arena.removeAllArenaCorpse();
		this.arena.removeAllArenaItem();
		
		for (GamePlayer gp : this.plugin.game.getPlayers()) {
			gp.getPlayer().sendTitle("§cL'équipe §l" + gameTeam.getName(), "§ca gagné cette manche !", 5, 40, 5);
			gp.getSkylander().removeInfoArmorStand();
			gp.setSkylander(null);
			gp.setVotedArena(null);
			gp.setReady(false);
		}
		
		new BukkitRunnable() {
			private Integer timer = 2;
			@Override
			public void run() {
				if (timer == 0) {
					plugin.game.checkVictory();
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void updateScoreboard() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Skylander skylander = gamePlayer.getSkylander();
			
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard scoreboard = manager.getNewScoreboard();
	        Objective objective = scoreboard.registerNewObjective("skylander", "dummy", "§8§l» §6§lSkylanders §8§l«");
	        
	        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	        objective.getScore(" ").setScore(6);
	        objective.getScore("§cDurée : §6" + timerRound + "s").setScore(5);
	        objective.getScore("§8----------------").setScore(4);
	        objective.getScore("§fSkylander : §e" + skylander.getName()).setScore(3);
	        objective.getScore("§fÉlément : §a" + skylander.getElement().getName()).setScore(2);     
	        objective.getScore("§f🗡 Force : §6" + SkylanderConverter.convertForce(skylander.getForce()) + "%").setScore(1);
	        objective.getScore("§f🛡 Résistance : §6" + SkylanderConverter.convertResis(skylander.getResis()) + "%").setScore(0);
	        
	        gamePlayer.getPlayer().setScoreboard(scoreboard);
		}
	}
}
