package fr.kamael.skylandersfight.game.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.air.JetVac;
import fr.kamael.skylandersfight.skylanders.air.LightningRod;
import fr.kamael.skylandersfight.skylanders.air.Scratch;
import fr.kamael.skylandersfight.skylanders.air.Warnado;
import fr.kamael.skylandersfight.skylanders.bogda.Cyroule;
import fr.kamael.skylandersfight.skylanders.bogda.DJMomone;
import fr.kamael.skylandersfight.skylanders.bogda.Higrishta;
import fr.kamael.skylandersfight.skylanders.bogda.LeRosatas;
import fr.kamael.skylandersfight.skylanders.bogda.Trayyks;
import fr.kamael.skylandersfight.skylanders.bogda.ZemZem;
import fr.kamael.skylandersfight.skylanders.eau.Chill;
import fr.kamael.skylandersfight.skylanders.eau.SlamBam;
import fr.kamael.skylandersfight.skylanders.eau.WhamShell;
import fr.kamael.skylandersfight.skylanders.feu.Eruptor;
import fr.kamael.skylandersfight.skylanders.feu.Flameslinger;
import fr.kamael.skylandersfight.skylanders.feu.Smolderdash;
import fr.kamael.skylandersfight.skylanders.feu.Sunburn;
import fr.kamael.skylandersfight.skylanders.magie.DoubleTrouble;
import fr.kamael.skylandersfight.skylanders.magie.Spyro;
import fr.kamael.skylandersfight.skylanders.magie.StarStrike;
import fr.kamael.skylandersfight.skylanders.magie.Voodood;
import fr.kamael.skylandersfight.skylanders.mort.Cynder;
import fr.kamael.skylandersfight.skylanders.mort.FrightRider;
import fr.kamael.skylandersfight.skylanders.mort.GhostRoaster;
import fr.kamael.skylandersfight.skylanders.mort.GrimCreeper;
import fr.kamael.skylandersfight.skylanders.tech.Boomer;
import fr.kamael.skylandersfight.skylanders.tech.Drobot;
import fr.kamael.skylandersfight.skylanders.tech.Sprocket;
import fr.kamael.skylandersfight.skylanders.tech.TriggerHappy;
import fr.kamael.skylandersfight.skylanders.terre.Bash;
import fr.kamael.skylandersfight.skylanders.terre.PrismBreak;
import fr.kamael.skylandersfight.skylanders.terre.Terrafin;
import fr.kamael.skylandersfight.skylanders.vie.Camo;
import fr.kamael.skylandersfight.skylanders.vie.StealthElf;
import fr.kamael.skylandersfight.skylanders.vie.StumpSmash;
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
		tech.add(TriggerHappy.getSignatureItem());
		tech.add(Drobot.getSignatureItem());
		tech.add(Boomer.getSignatureItem());
		tech.add(Sprocket.getSignatureItem());
		this.data.put(Element.TECH, tech);
		
		ArrayList<ItemStack> vie = new ArrayList<>();
		vie.add(StealthElf.getSignatureItem());
		vie.add(Camo.getSignatureItem());
		vie.add(StumpSmash.getSignatureItem());
		vie.add(ZooLou.getSignatureItem());
		this.data.put(Element.VIE, vie);
		
		ArrayList<ItemStack> mort = new ArrayList<>();
		mort.add(GhostRoaster.getSignatureItem());
		mort.add(Cynder.getSignatureItem());
		mort.add(FrightRider.getSignatureItem());
		mort.add(GrimCreeper.getSignatureItem());
		this.data.put(Element.MORT, mort);
		
		ArrayList<ItemStack> bogda = new ArrayList<>();
		// TODO : Remplir les Skylanders Bogda.
		bogda.add(Higrishta.getSignatureItem());
		bogda.add(Cyroule.getSignatureItem());
		bogda.add(ZemZem.getSignatureItem());
		bogda.add(DJMomone.getSignatureItem());
		bogda.add(LeRosatas.getSignatureItem());
		bogda.add(Trayyks.getSignatureItem());
		this.data.put(Element.BOGDA, bogda);
		
		ArrayList<ItemStack> feu = new ArrayList<>();
		feu.add(Eruptor.getSignatureItem());
		feu.add(Flameslinger.getSignatureItem());
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
		terre.add(Bash.getSignatureItem());
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
