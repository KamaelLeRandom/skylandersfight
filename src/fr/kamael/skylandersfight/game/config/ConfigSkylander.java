package fr.kamael.skylandersfight.game.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.skylanders.Element;

public class ConfigSkylander {
	private HashMap<Element, ArrayList<ItemStack>> data;
	
	public ConfigSkylander() {
		ArrayList<ItemStack> magie = new ArrayList<>();
		// TODO : Remplir les Skylanders Magie.
		this.data.put(Element.MAGIE, magie);

		ArrayList<ItemStack> tech = new ArrayList<>();
		// TODO : Remplir les Skylanders Tech.
		this.data.put(Element.TECH, tech);
		
		ArrayList<ItemStack> vie = new ArrayList<>();
		// TODO : Remplir les Skylanders Vie.
		this.data.put(Element.VIE, vie);
		
		ArrayList<ItemStack> mort = new ArrayList<>();
		// TODO : Remplir les Skylanders Mort.
		this.data.put(Element.MORT, mort);
		
		ArrayList<ItemStack> bogda = new ArrayList<>();
		// TODO : Remplir les Skylanders Bogda.
		this.data.put(Element.BOGDA, bogda);
		
		ArrayList<ItemStack> feu = new ArrayList<>();
		// TODO : Remplir les Skylanders Feu.
		this.data.put(Element.TECH, feu);
		
		ArrayList<ItemStack> eau = new ArrayList<>();
		// TODO : Remplir les Skylanders Eau.
		this.data.put(Element.EAU, eau);
		
		ArrayList<ItemStack> terre = new ArrayList<>();
		// TODO : Remplir les Skylanders Terre.
		this.data.put(Element.TERRE, terre);
		
		ArrayList<ItemStack> air = new ArrayList<>();
		// TODO : Remplir les Skylanders Air.
		this.data.put(Element.AIR, air);
	}
	
	public HashMap<Element, ArrayList<ItemStack>> getAllData() {
		return this.data;
	}
	
	public ArrayList<ItemStack> getDataByElement(Element element) {
		return this.data.get(element);
	}
}
