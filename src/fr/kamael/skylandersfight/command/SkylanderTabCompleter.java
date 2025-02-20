package fr.kamael.skylandersfight.command;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class SkylanderTabCompleter implements TabCompleter {
	
    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    	ArrayList<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("game");
            suggestions.add("config");
            suggestions.add("help");
        }

        return suggestions;
    }
}
