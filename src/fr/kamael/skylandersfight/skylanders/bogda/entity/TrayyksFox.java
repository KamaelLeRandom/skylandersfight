package fr.kamael.skylandersfight.skylanders.bogda.entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.entity.AggressiveFox;
import fr.kamael.skylandersfight.game.CustomEntity;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.bogda.Trayyks;
import net.minecraft.server.level.WorldServer;

public class TrayyksFox extends CustomEntity {
	
	public TrayyksFox(Trayyks trayyks, Location location) {
		super(trayyks, location);
	}
	
	@Override
	public void summon() {
		Player player = skylander.getPlayer();
	    WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
	    
		AggressiveFox fox = new AggressiveFox(player.getLocation(), Trayyks.nameFoxSecondVie, 10, skylander);
		world.addEntity(fox);

		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (fox == null || !fox.getBukkitEntity().isDead() || !plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 10);
		
		this.entity = fox.getBukkitEntity();
	}

	@Override
	public void onDamage(Skylander skylander) {
		skylander.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1, false, false));
		return; 
	}

}
