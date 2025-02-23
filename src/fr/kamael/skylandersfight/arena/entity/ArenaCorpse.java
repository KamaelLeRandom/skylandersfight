package fr.kamael.skylandersfight.arena.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class ArenaCorpse extends CustomEntity {

	public ArenaCorpse(Skylander skylander, Location location) {
		super(skylander, location);
	}
	
	public void summon() { 
		Player player = skylander.getPlayer();
		
		ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		as.setCanPickupItems(false);
		as.setVisible(false);
		as.setGravity(false);
		as.setHeadPose(new EulerAngle(5, 0, 0));
		as.setBodyPose(new EulerAngle(5, 0, 0));
		as.setLeftArmPose(new EulerAngle(5, 0, 0));
		as.setRightArmPose(new EulerAngle(5, 0, 0));
		as.getEquipment().setHelmet(ItemManager.createPlayerSkull(player));
		as.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
		
		this.entity = as;
		
		return;
	}
}
