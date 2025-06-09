package fr.kamael.skylandersfight.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;

public class LoreCommand implements CommandExecutor {
	private Plugin plugin = Plugin.plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		try {
			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("lore")) {
				Player player = (Player) sender;
				
			    ArrayList<Integer> discovered = plugin.loreUtils.getChapterDiscoveredPlayer(player.getUniqueId());

			    Inventory inv = Bukkit.createInventory(null, 27, Constants.inventoryLoreName);

			    for (int i = 0; i < Constants.numberOfChapterLore; i++) {
			        ItemStack item;
			        
			        if (discovered.contains(i)) {
			            item = new ItemStack(Material.WRITTEN_BOOK);
			            BookMeta meta = (BookMeta) item.getItemMeta();
			            meta.setDisplayName("§cChapitre #" + i);
						meta.setTitle("Chapitre #" + i);
						meta.setAuthor("Skylanders");
						
						List<String> pages = plugin.loreUtils.getChapterPages(i);
						for (String page : pages) {
						    meta.addPage(page);
						}

			            item.setItemMeta(meta);
			        } else {
			            item = new ItemStack(Material.BARRIER);
			            ItemMeta meta = item.getItemMeta();
			            meta.setDisplayName("§cInconnu #" + i);
			            item.setItemMeta(meta);
			        }
			        
			        inv.setItem(i, item);
			    }

			    player.openInventory(inv);
			    return true;
			}
			
			return false;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(LoreCommand, onCommand) : §7"+e.getMessage());	
			return false;
		}
	}
}
