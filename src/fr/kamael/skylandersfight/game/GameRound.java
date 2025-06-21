package fr.kamael.skylandersfight.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.arena.Arena;
import fr.kamael.skylandersfight.arena.ArenaInventory;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.SkylanderInventory;
import fr.kamael.skylandersfight.skylanders.bogda.Trayyks;
import fr.kamael.skylandersfight.utils.converter.ArenaConverter;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.TeamManager;

public class GameRound {
	private Plugin plugin = Plugin.plugin;
	private Arena arena;
	private Integer timerRound;
	
	public GameRound(Integer numeroRound) {
		this.timerRound = 0;
		this.plugin.game.setState(GameState.CHOOSING);
		
		remakeTeam();
		prechooseArena();
	}
	
	public Arena getArena() {
		return this.arena;
	}
	
	public Integer getTimer() {
		return this.timerRound;
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
				skylander.updateForce(+Constants.bonusElementaire);
				skylander.updateResis(-Constants.bonusElementaire);
			}
			
			if (skylander instanceof Trayyks) {
				((Trayyks) skylander).giveItemFromElement(element);
			}
		}
	}
	
	public void remakeTeam() {
		for (GameTeam team : plugin.game.getTeams()) {
			TeamManager.addAllPlayers(team.getTeam(), team.getPlayers());
		}
	}
	
	public void prechooseArena() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
			gamePlayer.getPlayer().sendTitle("§fChoix de l'§6Arène§f", "§fPlace aux votes.", 3, 25, 2);
			gamePlayer.getPlayer().sendMessage(Constants.prefixMessage + "Le choix de l'Arène va commencer !");
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				chooseArena();
				cancel();
				return;
			}
		}.runTaskLater(plugin, 30);
	}
	
	public void chooseArena() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.BLOCK_BARREL_OPEN, 1, 1);
			gamePlayer.getPlayer().openInventory(ArenaInventory.getInventory());
		}
		
		new BukkitRunnable() {
			private ArrayList<GamePlayer> listPlayers = plugin.game.getPlayers();
			
			@Override
			public void run() {
				Boolean isValid = true;
				
				for (GamePlayer gamePlayer : listPlayers) {
					if (gamePlayer.getVotedArena() == null) {
						isValid = false;
					}
				}
				
				if (isValid) {
					arena = ArenaConverter.convert(listPlayers.get(plugin.random.nextInt(listPlayers.size())).getVotedArena());
					prechooseSkylander();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void prechooseSkylander() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
			gamePlayer.getPlayer().sendTitle("§7Arène selectionnée§f", arena.getName(), 3, 50, 3);
			gamePlayer.getPlayer().sendMessage(Constants.prefixMessage + "L'§6Arène§f qui a été selectionée est " + arena.getName() + "§f, voici ses éléments possible : " + arena.getResumeElement() + "§f.");
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				chooseSkylander();
				cancel();
				return;
			}
		}.runTaskLater(plugin, 60);
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
					start();
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	public void start() {				
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Player player = gamePlayer.getPlayer();
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
			player.sendTitle("§7Le Round va débuter", "§fSoyez prêts.", 2, 35, 2);
		}
		
		new BukkitRunnable() {
			private Integer timer = 3;
			
			@Override
			public void run() {
				Bukkit.broadcastMessage(Constants.prefixMessage + "La manche démarre dans §b"+ timer +"§f secondes !");
				
				if (timer == 1) {
					preplay();
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void preplay() {
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Player player = gamePlayer.getPlayer();
			Skylander skylander = gamePlayer.getSkylander();
			gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
			gamePlayer.setActualTeam(gamePlayer.getInitialTeam());
			skylander.giveEquipement();
			skylander.setFullHealth();
			skylander.addMates(gamePlayer.getInitialTeam().getPlayers());
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, 200, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999, 200, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 0, false, false));
		}
		
		arena.teleportAllPlayer();
		arena.resetHeal();
		
		new BukkitRunnable() {
			private Integer timer = 3;
			@Override
			public void run() {
				if (timer == 0) {
					for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
						Player player = gamePlayer.getPlayer();
						player.removePotionEffect(PotionEffectType.BLINDNESS);
						player.removePotionEffect(PotionEffectType.SLOW);
						player.removePotionEffect(PotionEffectType.JUMP);
					}
					play();
					cancel();
					return;
				}
				
				for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
					Player player = gamePlayer.getPlayer();
					player.sendTitle("§fPréparez-vous", "§fDébut dans " + timer + " seconde.", 1, 18, 1);
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void play() {
		plugin.game.setState(GameState.FIGHTING);
		
		if (plugin.game.getConfig().getActiveEventMap())
			arena.event();
		
		for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
			Skylander skylander = gamePlayer.getSkylander();
			skylander.summonInfoArmorStand();
			skylander.onStart();
		}

		new BukkitRunnable() {
			private ArrayList<GamePlayer> listPlayers = plugin.game.getPlayers();
			private Integer timerItem = 15 + (plugin.random.nextInt(45)+1);
			private Integer timerDeathmatch = plugin.game.getConfig().getTimerDM() * 60;
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
				
				if (timer%5 == 0) {
					updateScoreboard();
				}
				
				if (timer%20 == 0) {
					timerRound++;
					timerItem--;
					
					if (
						timerRound == timerDeathmatch-60 ||
						timerRound == timerDeathmatch-30 ||
						timerRound == timerDeathmatch-10
					) {
						if (plugin.game.getConfig().getActiveDeathmatch()) {
							Integer sec = timerDeathmatch-timerRound;
							Bukkit.broadcastMessage(Constants.prefixMessage + "Le §cDeathmatch§f commence dans §c"+ sec + "§f secondes !");
						}
					}
					
					if (timerRound.equals(timerDeathmatch)) {
						if (plugin.game.getConfig().getActiveDeathmatch()) {
							Bukkit.broadcastMessage(Constants.prefixMessage + "L'heure du §cDeathmatch§f a sonné ! Bon courage.");
							arena.deathmatch();
							arena.teleportAllPlayer();
						}
					}
					
					if (timerRound == 5) {
						if (plugin.game.getConfig().getActiveBonusMap()) {
							chooseElement();
						}
					}
					
					if (plugin.game.getConfig().getActiveItem()) {
						if (timerItem == 0) {
							arena.summonArenaItem();
							timerItem = 15 + (plugin.random.nextInt(45)+1);
						}
					}
				}
								
				timer++;
			}
		}.runTaskTimer(plugin, 0, 1);
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
		
		plugin.statsUtils.updateDataAfterRound();
		
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
	    Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();

	    for (GamePlayer gamePlayer : plugin.game.getPlayers()) {
	        Player player = gamePlayer.getPlayer();
	        Skylander skylander = gamePlayer.getSkylander();

	        // Scoreboard individuel
	        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

	        // Copier les teams du scoreboard principal
	        for (Team mainTeam : main.getTeams()) {
	            Team team = scoreboard.registerNewTeam(mainTeam.getName());

	            team.setDisplayName(mainTeam.getDisplayName());
	            team.setPrefix(mainTeam.getPrefix());
	            team.setSuffix(mainTeam.getSuffix());
	            team.setColor(mainTeam.getColor());
	            team.setOption(Team.Option.COLLISION_RULE, mainTeam.getOption(Team.Option.COLLISION_RULE));
	            team.setOption(Team.Option.NAME_TAG_VISIBILITY, mainTeam.getOption(Team.Option.NAME_TAG_VISIBILITY));

	            // Ajouter les entrées si nécessaire
	            for (String entry : mainTeam.getEntries()) {
	                team.addEntry(entry);
	            }
	        }

	        // Supprimer l’ancien objectif s’il existe (sécurité)
	        Objective old = scoreboard.getObjective("sidebar");
	        if (old != null) old.unregister();

	        // Créer l’objectif de la sidebar
	        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", "§8§l» §6§lSkylanders §8§l«");
	        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

	        // Remplir les lignes du scoreboard
	        objective.getScore(" ").setScore(6);
	        objective.getScore("§cDurée : §6" + timerRound + "s").setScore(5);
	        objective.getScore("§8----------------").setScore(4);
	        objective.getScore("§fSkylander : " + skylander.getElement().getColor() + skylander.getName()).setScore(3);
	        objective.getScore("§fÉlément : " + skylander.getElement().getName()).setScore(2);     
	        objective.getScore("§f🗡 Force : §6" + SkylanderConverter.convertForce(skylander.getForce()) + "%").setScore(1);
	        objective.getScore("§f🛡 Résistance : §6" + SkylanderConverter.convertResis(skylander.getResis()) + "%").setScore(0);

	        // Appliquer le scoreboard personnalisé au joueur
	        player.setScoreboard(scoreboard);
	    }
	}




}
