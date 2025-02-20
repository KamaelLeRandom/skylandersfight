package fr.kamael.skylandersfight.game;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.skylanders.Skylander;

public class GamePlayer {
	private Player player;
	private Skylander skylander;
	private String votedArena;
	private Boolean ready;
	private GameTeam initialTeam;
	private GameTeam actualTeam;
	
	public GamePlayer(Player player) {
		this.player = player;
		this.ready = false;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Skylander getSkylander() {
		return this.skylander;
	}
	
	public Skylander setSkylander(Skylander skylander) {
		this.skylander = skylander;
		return this.skylander;
	}
	
	public String getVotedArena() {
		return this.votedArena;
	}
	
	public String setVotedArena(String votedArena) {
		this.votedArena = votedArena;
		return this.votedArena;
	}
	
	public Boolean isReady() {
		return this.ready;
	}
	
	public Boolean setReady(Boolean value) {
		this.ready = value;
		return this.ready;
	}
	
	public GameTeam getInitialTeam() {
		return this.initialTeam;
	}
	
	public GameTeam setInitialTeam(GameTeam newTeam) {
		this.initialTeam = newTeam;
		return this.initialTeam;
	}
	
	public GameTeam getActualTeam() {
		return this.actualTeam;
	}
	
	public GameTeam setActualTeam(GameTeam newTeam) {
		this.actualTeam = newTeam;
		return this.actualTeam;
	}
}
