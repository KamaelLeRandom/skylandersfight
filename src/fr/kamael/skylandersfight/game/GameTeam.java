package fr.kamael.skylandersfight.game;

import java.util.ArrayList;

public class GameTeam {
	private String name;
	private Integer nbPoint;
	private Integer nbKill;
	private ArrayList<GamePlayer> players;
	
	public GameTeam(String name, ArrayList<GamePlayer> players) {
		this.name = name;
		this.nbPoint = 0;
		this.nbKill = 0;
		this.players = players;
		
		for (GamePlayer player : players) {
			player.setInitialTeam(this);
		}
	}
	
	public GameTeam(String name, GamePlayer player) {
		this.name = name;
		this.nbPoint = 0;
		this.nbKill = 0;
		this.players = new ArrayList<GamePlayer>();
		this.players.add(player);

		player.setInitialTeam(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer getNbPoint() {
		return this.nbPoint;
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
