package fr.kamael.skylandersfight.arena.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.arena.map.NouveauLabogda;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.Skylander;

public class ScientifiqueLabogda extends CustomEntity {

	public ScientifiqueLabogda(Location location) {
		super(null, location);
	}
	
	@Override
	public void summon() {		
		Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setHealth(20);
		zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		zombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		zombie.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		zombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		zombie.setRemoveWhenFarAway(false);
		this.entity = zombie;
	}
	
	@Override
	public void onDeath() {
		Zombie zombie = (Zombie) this.entity;
		
		if (zombie.getKiller() instanceof Player && plugin.game.getRound().getArena() instanceof NouveauLabogda) {
			Player player = (Player) zombie.getKiller();
			Skylander skylander = plugin.game.getPlayer(player).getSkylander();
			
			if (skylander.isAlive()) {
				((NouveauLabogda) plugin.game.getRound().getArena()).updateMobKilledBySkylander(skylander);
			}
		}
		
		return; 
	}
}
