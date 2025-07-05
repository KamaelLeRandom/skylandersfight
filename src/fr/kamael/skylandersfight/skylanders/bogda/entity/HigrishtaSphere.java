package fr.kamael.skylandersfight.skylanders.bogda.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.bogda.Higrishta;
import fr.kamael.skylandersfight.utils.SpellUtils;

public class HigrishtaSphere extends CustomEntity {
	public static final Integer tickMaxTimer = 200;
	
	public HigrishtaSphere(Higrishta higrishta, Location location) {
		super(higrishta, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
		World world = player.getWorld();
		
        ArmorStand sphere = world.spawn(location, ArmorStand.class);
        sphere.setInvisible(true);
        sphere.setSmall(true);
        sphere.setGravity(false);
        sphere.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
		
        Skylander skylanderTarget = SpellUtils.nearClosePlayer(plugin, skylander, sphere, 50.);
        if (skylanderTarget != null) {
        	Player playerTarget = skylanderTarget.getPlayer();
        	
            new BukkitRunnable() {
                private Integer timer = tickMaxTimer;

                @Override
                public void run() {
                    if (timer == 0 || entity == null || entity.isDead() || !skylander.isAlive() || !plugin.game.isState(GameState.FIGHTING)) {
                    	if (entity != null)
                    		removeEntity();
                        cancel();
                        return;
                    }

                    Location sphereLoc = sphere.getLocation();
                    Location targetLoc = playerTarget.getLocation().add(0, 1, 0);
                    Vector direction = targetLoc.toVector().subtract(sphereLoc.toVector()).normalize().multiply(0.5);
                    sphere.teleport(sphereLoc.add(direction));

                    // Effets visuels
                    world.spawnParticle(Particle.PORTAL, sphere.getLocation(), 10, 0.2, 0.2, 0.2, 0.05);
                    world.playSound(sphereLoc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.5f, 1.5f);

                    // Collision
                    if (sphereLoc.distanceSquared(targetLoc) < 1.0) {
                        world.spawnParticle(Particle.EXPLOSION_LARGE, sphereLoc, 1);
                        world.playSound(sphereLoc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.6f);
                        playerTarget.damage(Higrishta.damageFirstSpell, player);
                        sphere.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 2);
        }        
     
		this.entity = sphere;
	}

}
