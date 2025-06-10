package fr.kamael.skylandersfight.skylanders.vie.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.vie.ZooLou;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class ZooLouWolf extends CustomEntity {
	
	public ZooLouWolf(ZooLou zoolou, Location location) {
		super(zoolou, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
		wolf.setOwner(player);
		wolf.setAdult();
		wolf.setHealth(ZooLou.healthWolfFirstSpell);
		wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		
		this.entity = wolf;
	}
	
	public void onDamage(Skylander skylander) {
		if (skylander.getElement().equals(Element.VIE))
			SpellUtils.heal(this.skylander, ZooLou.healFirstSpell, true);
		return; 
	}
}
