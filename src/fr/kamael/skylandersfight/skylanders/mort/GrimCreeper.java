package fr.kamael.skylandersfight.skylanders.mort;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.mort.entity.GrimCreeperArmor;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class GrimCreeper extends Skylander {
	public static final String name = "Grim Creeper";
	
	public static final String nameWeapon = "§7";
	public static final String namePassif = "§7";
	public static final Integer nbHitFirstBonusPassif = 10;
	public static final Integer nbHitSecondBonusPassif = 25;
	public static final Double pourcentExecSecondBonusPassif = 0.2;
	public static final Integer nbHitThirdBonusPassif = 50;
	
	public static final String nameFirstSpell = "§7";
	public static final Integer timerFirstSpell = 5;
	
	public static final String nameSecondSpell = "§7Séparation";
	public static final Double nerfResisSecondSpell = 0.2;
	public static final Integer timerSecondSpell = 30;
	
	private Integer nbHitPassif = 0;
	
	private GrimCreeperArmor secondSpellArmor = null;
	private Boolean secondSpellActive = false;
	
	public Boolean onDamageSword(Skylander skylanderHit) {
		nbHitPassif++;
		player.setLevel(nbHitPassif);
		
		if (nbHitPassif > nbHitFirstBonusPassif)
			skylanderHit.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0, false, false));
		
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
}
