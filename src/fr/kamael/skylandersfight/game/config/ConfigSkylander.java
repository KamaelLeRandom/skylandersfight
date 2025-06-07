package fr.kamael.skylandersfight.game.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.air.JetVac;
import fr.kamael.skylandersfight.skylanders.air.LightningRod;
import fr.kamael.skylandersfight.skylanders.air.Scratch;
import fr.kamael.skylandersfight.skylanders.air.Warnado;
import fr.kamael.skylandersfight.skylanders.eau.Chill;
import fr.kamael.skylandersfight.skylanders.eau.SlamBam;
import fr.kamael.skylandersfight.skylanders.eau.WhamShell;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.skylanders.feu.Smolderdash;
import fr.kamael.skylandersfight.skylanders.feu.Sunburn;
import fr.kamael.skylandersfight.skylanders.magie.DoubleTrouble;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.skylanders.magie.StarStrike;
import fr.kamael.skylandersfight.skylanders.magie.Voodood;
import fr.kamael.skylandersfight.skylanders.mort.GhostRoaster;
import fr.kamael.skylandersfight.skylanders.mort.GrimCreeper;
import fr.kamael.skylandersfight.skylanders.tech.Drobot;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.skylanders.tech.TriggerHappy;
import fr.kamael.skylandersfight.skylanders.terre.PrismBreak;
import fr.kamael.skylandersfight.skylanders.terre.Terrafin;
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;

public class ConfigSkylander {
	private HashMap<Element, ArrayList<ItemStack>> data;
	
	public ConfigSkylander() {
		this.data = new HashMap<Element, ArrayList<ItemStack>>();
		
		ArrayList<ItemStack> magie = new ArrayList<>();
		magie.add(Spyro.getSignatureItem());
		magie.add(DoubleTrouble.getSignatureItem());
		magie.add(Voodood.getSignatureItem());
		magie.add(StarStrike.getSignatureItem());
		this.data.put(Element.MAGIE, magie);

		ArrayList<ItemStack> tech = new ArrayList<>();
		// TODO : Remplir les Skylanders Tech.
		tech.add(TriggerHappy.getSignatureItem());
		tech.add(Drobot.getSignatureItem());
		tech.add(Sprocket.getSignatureItem());
		this.data.put(Element.TECH, tech);
		
		ArrayList<ItemStack> vie = new ArrayList<>();
		// TODO : Remplir les Skylanders Vie.
		vie.add(StealthElf.getSignatureItem());
		vie.add(ZooLou.getSignatureItem());
		this.data.put(Element.VIE, vie);
		
		ArrayList<ItemStack> mort = new ArrayList<>();
		// TODO : Remplir les Skylanders Mort.
		mort.add(GhostRoaster.getSignatureItem());
		mort.add(GrimCreeper.getSignatureItem());
		this.data.put(Element.MORT, mort);
		
		ArrayList<ItemStack> bogda = new ArrayList<>();
		// TODO : Remplir les Skylanders Bogda.
		this.data.put(Element.BOGDA, bogda);
		
		ArrayList<ItemStack> feu = new ArrayList<>();
		// TODO : Remplir les Skylanders Feu.
		feu.add(Eruptor.getSignatureItem());
		feu.add(Sunburn.getSignatureItem());
		
		feu.add(Smolderdash.getSignatureItem());
		this.data.put(Element.FEU, feu);
		
		ArrayList<ItemStack> eau = new ArrayList<>();
		// TODO : Remplir les Skylanders Eau.
		eau.add(SlamBam.getSignatureItem());
		eau.add(WhamShell.getSignatureItem());
		eau.add(Chill.getSignatureItem());
		this.data.put(Element.EAU, eau);
		
		ArrayList<ItemStack> terre = new ArrayList<>();
		// TODO : Remplir les Skylanders Terre.
		terre.add(Terrafin.getSignatureItem());
		terre.add(PrismBreak.getSignatureItem());
		this.data.put(Element.TERRE, terre);
		
		ArrayList<ItemStack> air = new ArrayList<>();
		air.add(LightningRod.getSignatureItem());
		air.add(Warnado.getSignatureItem());
		air.add(JetVac.getSignatureItem());
		air.add(Scratch.getSignatureItem());
		this.data.put(Element.AIR, air);
	}
	
	public HashMap<Element, ArrayList<ItemStack>> getAllData() {
		return this.data;
	}
	
	public ArrayList<ItemStack> getDataByElement(Element element) {
		return this.data.get(element);
	}
}
