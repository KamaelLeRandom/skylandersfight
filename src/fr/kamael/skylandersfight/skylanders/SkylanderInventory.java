package fr.kamael.skylandersfight.skylanders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.config.Config;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class SkylanderInventory {
	
	public static Inventory getRandomInventory() {
		try {
			Plugin plugin = Plugin.plugin;
			
			Config config = plugin.game.getConfig();
			Random random = plugin.random;
			Integer taille = 18 + (config.getNbSkylanderLine() * 9);
			HashMap<Element, ArrayList<ItemStack>> data = plugin.game.getConfigSkylander().getAllData();
			Inventory classic = Bukkit.createInventory(null, taille, Constants.inventorySkylanderName);
			
			for (Integer i = 0; i < 9; i++) {
				classic.setItem(i, ItemManager.getInventoryGlass());
				classic.setItem(i + (taille - 9), ItemManager.getInventoryGlass());
			}
			classic.setItem(taille-5, ItemManager.makeBasicItem(Material.COMPASS, "§7Aléatoire", 1));
			
			Integer idxColumn = 0;
			Element[] elements = { Element.MAGIE, Element.TECH, Element.VIE, Element.MORT, Element.BOGDA, Element.FEU, Element.EAU, Element.TERRE, Element.AIR, };
			for (Element element : elements) {
				ArrayList<ItemStack> dataElement = data.get(element);

				if (dataElement != null) {
					// S'il n'y a pas assez de Skylander.
					if (dataElement.size() < config.getNbSkylanderLine()) {
						Integer idxRow = 0;
						for (Integer i = 0; i < config.getNbSkylanderLine(); i++) {
							if (i >= dataElement.size()) {
								classic.setItem(9 + idxColumn + (9 * idxRow), ItemManager.makeBasicItem(Material.BARRIER, "§cX", 1));
							} else {
								classic.setItem(9 + idxColumn + (9 * idxRow), dataElement.get(i));
							}
							idxRow++;
						}
					}
					// S'il y a pile assez.
					else if (dataElement.size() == config.getNbSkylanderLine()) {
						Integer idxRow = 0;
						for (ItemStack it : dataElement) {
							classic.setItem(9 + idxColumn + (9 * idxRow), it);
							idxRow++;
						}
					}
					// S'il y a plus, on tire aléatoirement.
					else {
						ArrayList<Integer> idxSkylandersAleatoire = new ArrayList<>();
						Integer idxAleatoire = random.nextInt(dataElement.size());
						for (Integer i = 0; i < config.getNbSkylanderLine(); i++) {
							while (idxSkylandersAleatoire.contains(idxAleatoire)) {
								idxAleatoire = random.nextInt(dataElement.size());
							}
							idxSkylandersAleatoire.add(idxAleatoire);
						}
						Collections.sort(idxSkylandersAleatoire);
						Integer idxRow = 0;
						for (Integer idxSkylander : idxSkylandersAleatoire) {
							classic.setItem(9 + idxColumn + (9 * idxRow), dataElement.get(idxSkylander));
							idxRow++;
						}
					}	
				}
				
				idxColumn++;
			}
			
			return classic;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(SkylanderInventory, getRandomInventory) : §7"+e.getMessage());	
			return null;
		}
	}
}
