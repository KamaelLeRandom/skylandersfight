package fr.kamael.skylandersfight.game;

import fr.kamael.skylandersfight.Plugin;

public class Game {
	protected Plugin plugin = Plugin.plugin;
	protected GameState state;
	
	public void setState(GameState state) {
		this.state = state;
	}
	
	public Boolean isState(GameState state) {
		if (this.state.equals(state)) {
			return true;
		}
		return false;
	}
}
