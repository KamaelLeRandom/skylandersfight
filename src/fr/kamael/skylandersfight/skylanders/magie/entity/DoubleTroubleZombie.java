package fr.kamael.skylandersfight.skylanders.magie.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.magie.DoubleTrouble;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class DoubleTroubleZombie extends CustomEntity {

	public DoubleTroubleZombie(DoubleTrouble doubletrouble, Location location) {
		super(doubletrouble, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		
		Zombie zombie = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setBaby();
		zombie.setHealth(10);
		zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		zombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		zombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		zombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!zombie.isDead() || zombie == null || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				zombie.setTarget(SpellUtils.nearClosePlayer(plugin, zombie, 30.).getPlayer());
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = zombie;
	}
	
	public void onDamage(Skylander skylander) { 
		skylander.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2, false, false));
		return; 
	}
}
