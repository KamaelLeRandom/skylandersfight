package fr.kamael.skylandersfight.utils.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import fr.kamael.skylandersfight.game.GamePlayer;

public class TeamManager {
	public static final List<ChatColor> COLORS = List.of(
	        ChatColor.RED,
	        ChatColor.BLUE,
	        ChatColor.GREEN,
	        ChatColor.YELLOW,
	        ChatColor.LIGHT_PURPLE,
	        ChatColor.GOLD,
	        ChatColor.GRAY,
	        ChatColor.AQUA,
	        ChatColor.DARK_RED,
	        ChatColor.DARK_BLUE,
	        ChatColor.DARK_GREEN,
	        ChatColor.DARK_AQUA,
	        ChatColor.DARK_PURPLE,
	        ChatColor.BLACK,
	        ChatColor.WHITE,
	        ChatColor.DARK_GRAY
	);

	public static Team create(String teamName, ChatColor color) {
	    Scoreboard	 board = Bukkit.getScoreboardManager().getMainScoreboard();

	    if (board.getTeam(teamName) != null) {
	        board.getTeam(teamName).unregister();
	    }

	    Team team = board.registerNewTeam(teamName);
	    team.setColor(color);
	    team.setAllowFriendlyFire(true);
	    team.setCanSeeFriendlyInvisibles(false);

	    return team;
	}
	
    public static boolean addPlayer(Team team, GamePlayer player) {
        if (team == null || player == null) {
            return false;
        }

        String playerName = player.getPlayer().getName();

        if (team.hasEntry(playerName))
            return false;

        team.addEntry(playerName);
        
        return true;
    }
	
	public static boolean addAllPlayers(Team team, List<GamePlayer> players) {
	    if (team == null || players == null || players.isEmpty())
	        return false;

	    for (GamePlayer player : players) {
	        addPlayer(team, player);
	    }

	    return true;
	}
	
    public static Boolean removePlayer(Team team, GamePlayer player) {
        if (team == null || player == null)
            return false;

        String playerName = player.getPlayer().getName();

        if (!team.hasEntry(playerName))
            return false;

        team.removeEntry(playerName);
        
        return true;
    }
	
	public static Boolean removeAllPlayer(Team team) {
        if (team == null)
            return false;
		
        Set<String> members = new HashSet<>(team.getEntries());
        for (String member : members) {
            team.removeEntry(member);
        }

        return true;
	}
	
    public static Boolean delete(String teamName) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(teamName);

        if (team == null)
            return false;

        team.unregister();
        
        return true;
    }
    
    public static Boolean deleteAll() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Team team : board.getTeams()) {
            team.unregister();
        }

        return true;
    }
}
