package fr.kamael.skylandersfight.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.player.EntityHuman;

public class AggressiveGolem extends EntityIronGolem {
	private Plugin plugin = Plugin.plugin;
	
	public AggressiveGolem(Location location, String name, Integer life, Skylander skylander) {
		super(EntityTypes.P, ((CraftWorld) location.getWorld()).getHandle());
		this.setPosition(location.getX(), location.getY(), location.getZ());
		this.setCustomName(new ChatComponentText(name));
		this.setCustomNameVisible(true);
		this.setHealth(life);

		// targetSelector
		this.bQ.a(); // Réinitialiser les targetSelector initiaux
		this.bQ.a(1, new PathfinderGoalNearestAttackableTarget<>(
			    this,
			    EntityHuman.class,
			    10,
			    true,
			    false,
			    (entity) -> {
			    	Entity bukkitEntity = entity.getBukkitEntity();
			    	if (bukkitEntity instanceof Player && plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
			    		Player playerTarget = (Player) bukkitEntity;
			    		Skylander skylanderTarget = plugin.game.getPlayer(playerTarget).getSkylander();
			    		
			    		return (!skylander.equals(skylanderTarget) && !skylander.getMates().contains(skylanderTarget));
			    	}
			    	return true;
			    }
			));
		
		// goalSelector 
        this.bP.a(); // Réinitialiser les goalSelector initiaux
        this.bP.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 1.0D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));
	}
}
