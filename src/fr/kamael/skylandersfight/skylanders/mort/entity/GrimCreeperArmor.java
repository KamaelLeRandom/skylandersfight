package fr.kamael.skylandersfight.skylanders.mort.entity;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.mort.GrimCreeper;

public class GrimCreeperArmor extends CustomEntity {
	
	public GrimCreeperArmor(GrimCreeper grimcreeper, Location location) {
		super(grimcreeper, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		
		as.setCustomNameVisible(true );
		as.setCustomName("§7Armure de §c"+player.getName());
		as.setBasePlate(false);
		as.getEquipment().setHelmet(player.getInventory().getHelmet());
		as.getEquipment().setChestplate(player.getInventory().getChestplate());
		as.getEquipment().setLeggings(player.getInventory().getLeggings());
		as.getEquipment().setBoots(player.getInventory().getBoots());
		
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.AQUA, 1.0F);
		
		new BukkitRunnable() {
			private Integer timer = 30;
			@Override
			public void run() {				
				if (as == null || as.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
					removeEntity();
					cancel();
					return;
				}
				
				Location start = player.getLocation();
				Location end = entity.getLocation();
		        Vector direction = end.toVector().subtract(start.toVector());
		        Double step = 0.2;
		        Double distance = direction.length();
		        direction.normalize(); 

		        for (double d = 0; d <= distance; d += step) {
		            Location currentLocation = start.clone().add(direction.clone().multiply(d));
		            currentLocation.add(0., 1., 0.);
		            start.getWorld().spawnParticle(Particle.REDSTONE, currentLocation, 1, dustOptions);
		        }
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 10);
		
		entity = as;
	}
	
	@Override
	public void onHit(Skylander skylander) {
		Player player = this.skylander.getPlayer();
		player.playSound(player.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
		player.sendTitle("§7Séparation", "§cVotre §7Armure§c est attaqué!", 1, 20, 1);
		player.damage(5, skylander.getPlayer());
		return;
	}
}
