package fr.kamael.skylandersfight.skylanders.mort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.mort.entity.GrimCreeperArmor;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class GrimCreeper extends Skylander {
	public static final String name = "Grim Creeper";
	
	public static final String nameWeapon = "§7Faux des Âmes";
	public static final String namePassif = "§7Faucheur des Âmes";
	public static final Integer nbHitFirstBonusPassif = 10;
	public static final Integer timerSlowFirstBonusPassif = 5;
	public static final Integer nbHitSecondBonusPassif = 25;
	public static final Double pourcentExecSecondBonusPassif = 0.2;
	public static final Integer nbHitThirdBonusPassif = 50;
	
	public static final String nameFirstSpell = "§7Ruée Circulaire";
	public static final Integer damageFirstSpell = 5; 
	public static final Double vectorPowerFirstSpell = 0.8;
	public static final Integer timerFirstSpell = 15;
	
	public static final String nameSecondSpell = "§7Séparation";
	public static final Double nerfResisSecondSpell = 0.2;
	public static final Integer timerSecondSpell = 30;
	
	private Integer nbHitPassif = 0;
	
	private GrimCreeperArmor secondSpellArmor = null;
	private Boolean secondSpellActive = false;
	
	public GrimCreeper(Player player) {
		super(player, Element.MORT, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Boolean onDamageSword(Skylander skylanderHit) {
		nbHitPassif++;
		player.setLevel(nbHitPassif);
		
		if (nbHitPassif == nbHitFirstBonusPassif)
			player.sendMessage(Constants.prefixMessage + "Vous avez recolté " + nbHitFirstBonusPassif + " Âmes, vous infligez maintenant l'effet Lenteur pendant " + timerSlowFirstBonusPassif + " seconde à chaque coup que vous infligez.");
		if (nbHitPassif == nbHitSecondBonusPassif)
			player.sendMessage(Constants.prefixMessage + "Vous avez recolté " + nbHitSecondBonusPassif + " Âmes, vous executez les adversaires lorsqu'ils ont moins de " + pourcentExecSecondBonusPassif*100 + "% de leur vie.");
		if (nbHitPassif == nbHitThirdBonusPassif)
			player.sendMessage(Constants.prefixMessage + "Vous avez recolté " + nbHitThirdBonusPassif + " Âmes, vous retirez de manière permanents les dégats que vous infligez.");
		
		if (nbHitPassif > nbHitFirstBonusPassif)
			skylanderHit.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, timerSlowFirstBonusPassif*20, 0, false, false));
		
		return false; 
	}
	
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (nbHitPassif > nbHitSecondBonusPassif && SpellUtils.getPourcentLife(skylanderHit) <= pourcentExecSecondBonusPassif) {
			return 9999.;
		}
		
		if (nbHitPassif > nbHitThirdBonusPassif) {
			SpellUtils.changeLife(skylanderHit, -damage);
		}

		return damage;
	}
	
	public void firstSpell_Dash() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameFirstSpell + "§f.");
			
			SpellUtils.dash(
				this, 
				vectorPowerFirstSpell, 
				2.5,
				8,
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.damage(damageFirstSpell);
				}, 
				(location) -> {
					World world = location.getWorld();
			        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.0f); // Aqua

			        Double radius = 1.5;
			        Integer points = 32;

			        for (int i = 0; i < points; i++) {
			        	Double angle = 2 * Math.PI * i / points;
			        	Double x = radius * Math.cos(angle);
			        	Double z = radius * Math.sin(angle);
			            Location particleLocation = location.clone().add(x, 0.1, z);
			            world.spawnParticle(Particle.REDSTONE, particleLocation, 0, 0, 0, 0, 0, dustOptions);
			        }
				}
			);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Separation() {
		if (secondSpellActive) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'arrêter votre compétence "+ nameSecondSpell +"§f.");
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.teleport(secondSpellArmor.getEntity().getLocation());
			resis -= nerfResisSecondSpell;
			secondSpellActive = false;
			secondSpellArmor.removeEntity();
			ItemManager.giveColorArmor(player, Color.BLACK);
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
		
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utilisez votre compétence "+ nameSecondSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
			resis += nerfResisSecondSpell;
			secondSpellActive = true;
			secondSpellArmor = new GrimCreeperArmor(this, player.getLocation());
			ItemManager.giveColorArmor(player, Color.AQUA);
			
			return;
		}
	}

	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, lorsque vous frappez un joueur vous gagnez un fragment d'Âme, vous gagnez des bonus cumulatifs suivant les nombres d'Âmes§f.");
	    player.sendMessage("\n");
	    player.sendMessage("    ≫ <" + nbHitFirstBonusPassif + " : vous infligez un effet de Ralentissement aux joueurs que vous frappez.");
	    player.sendMessage("\n");
	    player.sendMessage("    ≫ <" + nbHitSecondBonusPassif + " : vous executez les joueurs sous " + pourcentExecSecondBonusPassif*100 + "% de leur vie.");
	    player.sendMessage("\n");
	    player.sendMessage("    ≫ <" + nbHitThirdBonusPassif + " : vos dégats retire de la vie permanentes.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous effectuez une §3petit ruée§f vers l'avant infligeant §3" + damageFirstSpell + " dégats§f aux joueurs autour de vous. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous laissez votre armure sur place, vous gagnez l'effet §aVitesse 2§f cependant vous perdez §c" + nerfResisSecondSpell*100 + "% de Résistance§f. Vous pouvez à tout moment §3revenir à votre armure§f en réactivant la compétence. Si votre §carmure est frappé§f par un joueur vous §csubirez des dégats§f. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7"+ name +"§f est un Skylander §cmélée§f capable");
		lore.add("§fd'augmenter sa puissance au fil de la partie.");
		
		ItemStack item = new ItemStack(Material.DIAMOND_HOE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous effectuez une ruée vers l'avant§f", "qui inflige "+ damageFirstSpell +" dégats aux joueurs proches§f.");
		
		ItemStack item = new ItemStack(Material.STICK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameFirstSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemSecondSpell() {
		List<String> lore = Arrays.asList("§fVous laissez votre armure sur place, vous gagnez l'effet Vitesse 2", "§fcependant vous écopez d'un malus de "+ nerfResisSecondSpell*100 +"% de Résistance§f.");
		
		ItemStack item = new ItemStack(Material.ARMOR_STAND, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack getItemWeapon() {
		ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
