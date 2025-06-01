package fr.kamael.skylandersfight.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import fr.kamael.skylandersfight.utils.manager.TeamManager;

public class GameTeam {
	private String name;
	private Integer nbPoint;
	private Integer nbKill;
	private ArrayList<GamePlayer> players;
	private Team team;
	private ChatColor color;
	
	public GameTeam(String name, ArrayList<GamePlayer> players, ChatColor color) {
		this.name = name;
		this.nbPoint = 0;
		this.nbKill = 0;
		this.players = players;
		this.color = color;
		this.team = TeamManager.create(name, color);
		TeamManager.addAllPlayers(team, players);
		
		for (GamePlayer player : players) {
			player.setInitialTeam(this);
		}
	}
	
	public GameTeam(String name, GamePlayer player, ChatColor color) {
		this.name = name;
		this.nbPoint = 0;
		this.nbKill = 0;
		this.players = new ArrayList<GamePlayer>();
		this.players.add(player);
		this.color = color;
		this.team = TeamManager.create(name, color);
		TeamManager.addPlayer(team, player);
		
		player.setInitialTeam(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Team getTeam() {
		return this.team;
	}
	
	public Integer getNbPoint() {
		return this.nbPoint;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public Integer updateNbPoint(Integer value) {
		this.nbPoint += value;
		return this.nbPoint;
	}
	
	public Integer getNbKill() {
		return this.nbKill;
	}
	
	public Integer updateNbKill(Integer value) {
		this.nbKill += value;
		return this.nbKill;
	}
	
	public ArrayList<GamePlayer> getPlayers() {
		return this.players;
	}
}
