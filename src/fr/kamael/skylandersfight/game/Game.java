package fr.kamael.skylandersfight.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.Plugin;

public class Game {
	protected Plugin plugin = Plugin.plugin;
	protected GameState state;
	protected ArrayList<GamePlayer> listPlayers;
	protected ArrayList<GameTeam> listTeams;
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public Boolean isState(GameState state) {
		if (this.state.equals(state)) {
			return true;
		}
		return false;
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
}
