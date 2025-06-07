package fr.kamael.skylandersfight.skylanders.tech.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.tech.Boomer;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class BoomerTrapTNT extends CustomEntity {
	
	public BoomerTrapTNT(Boomer boomer, Location location) {
		super(boomer, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().clone().subtract(0, 0.4, 0), EntityType.ARMOR_STAND);
        armorStand.setInvisible(true);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(new ItemStack(Material.TNT));
        
        new BukkitRunnable() {
			private Boolean hit = false;
        	
			@Override
			public void run() {
				if (hit || armorStand == null || armorStand.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					removeEntity();
					cancel();
					return;
				}
				
				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, skylander, armorStand.getLocation(), Boomer.rangeTrapFirstSpell, 0.5, Boomer.rangeTrapFirstSpell)) {
					Player playerHit = skylanderHit.getPlayer();
					playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					playerHit.sendMessage(Constants.prefixMessage + "Vous venez de marcher sur une " + Boomer.nameFirstSpell + "§f de §6" + player.getName() + "§f.");
					playerHit.damage(Boomer.damageTrapFirstSpell, player);
					playerHit.spawnParticle(Particle.EXPLOSION_NORMAL, armorStand.getLocation(), 1);
					hit = true;
				}
			}
		}.runTaskTimer(plugin, 0, 3);

		
		this.entity = armorStand;
	}
	
	@Override
	public void onHit(Skylander skylander) {
        entity.getWorld().spawnParticle(Particle.SMOKE_LARGE, entity.getLocation().add(0, 1, 0), 20, 0.2, 0.2, 0.2, 0.01);
		removeEntity();
		return; 
	}
}
