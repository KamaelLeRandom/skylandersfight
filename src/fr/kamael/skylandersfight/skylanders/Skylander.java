package fr.kamael.skylandersfight.skylanders;

import java.util.ArrayList;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Plugin;

public class Skylander {
	protected Plugin plugin = Plugin.plugin;
	protected ArmorStand info;
	protected Player player;
	protected Element element;
	protected String name;
	protected Boolean alive;
	protected Double force;
	protected Double resis;
	protected ArrayList<Cooldown> cooldowns;
	protected ArrayList<Status> status;
	protected ArrayList<Skylander> mates;
	
	public Skylander(Player player, Element element, String name) {
		this.player = player;
		this.element = element;
		this.name = name;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Element getElement() {
		return this.element;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Double getForce() {
		return this.force;
	}
	
	public Double updateForce(Double value) {
		this.force += value;
		return this.force;
	}
	
	public Double getResis() {
		return this.resis;
	}
	
	public Double updateResis(Double value) {
		this.resis += value;
		return this.resis;
	}
	
	public Boolean isAlive() {
		return this.alive;
	}
	
	public void summonInfoArmorStand() {
		this.info = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, 2.2, 0), EntityType.ARMOR_STAND);
		this.info.setMarker(true);
		this.info.setVisible(false);
		this.info.setCustomNameVisible(false);
		this.info.setCustomName("");
		this.info.setSmall(true);
	}
	
	public void updateInfoArmorStand() {
		this.info.teleport(player.getLocation().add(0, 2.2, 0));
		
		if (player.isSneaking() || status.contains(Status.INVISIBLE)) {
			this.info.setCustomNameVisible(false);
		} else {
			String msg = "§7|";
			
			if (status.contains(Status.NOTAKEDAMAGE))
				msg += "§aInvulnérable§7|";
			
			if (status.contains(Status.NOMOVE))
				msg += "§cImmobilisé§7|";
			
			if (status.contains(Status.NOMAKEDAMAGE))
				msg += "§cDésarmé§7|";
			
			if (status.contains(Status.NOSPELL))
				msg += "§cSilence§7|";
			
			if (status.contains(Status.NOHEAL))
				msg += "§cHémorragie§7|";
					
			if (msg.equals("§7|")) {
				this.info.setCustomNameVisible(false);
			} else {
				this.info.setCustomNameVisible(true);
				this.info.setCustomName(msg);	
			}
		}
	}
	
	public void removeInfoArmorStand() {
		this.info.remove();
		this.info = null;
	}
	
	public void addStatus(Integer ticks, Status... status) {
		ArrayList<Status> statusToApply = new ArrayList<>();
		for (Status s : status) {
			if (!this.status.contains(s)) {
				statusToApply.add(s);
			}
		}
		
		this.status.addAll(statusToApply);
		
		if (ticks != null) {
			new BukkitRunnable() {
				private Integer timer = ticks;
				
				@Override
				public void run() {
					if (timer <= 0) {
						for (Status sToApply : statusToApply) {
							removeStatus(sToApply);
						}
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 1);
		}
	}
	
	public void removeStatus(Status status) {
		if (this.status.contains(status)) {
			this.status.remove(status);	
		}
	}
	
	public void removeAllStatus() {
		this.status.removeAll(status);
	}
	
	public Boolean checkStatus(Status status) {
		if (this.status.contains(status)) {
			return true;
		}
		return false;
	}
	
	public void addCooldown(String name, Integer seconds) {
		this.cooldowns.add(new Cooldown(this, name, seconds));
	}
	
	public void removeCooldown(String name) {
		this.cooldowns.removeIf(entry -> entry.getName().equals(name));
	}
	
	public void removeCooldown(Cooldown cooldown) {
		this.cooldowns.remove(cooldown);
	}
	
	public void removeAllCooldown() {
		this.cooldowns.removeAll(cooldowns);
	}
	
	public Boolean checkCooldown(String name, Boolean showTimer) {
		for (Cooldown cooldown : cooldowns) {
			if (cooldown.getName().equals(name)) {
				if (showTimer) {
					cooldown.printCooldown();
				}
				return false;
			}
		}
		return true;
	}

	/// --- Méthodes à surcharger.
	
	public void giveEquipement() { return; }
	
	public void sendDescription() { return; }
	
	public void onStart() { return; } // Quand le round commence. 
	
	public void onSneak() { return; } // Quand un joueur se met en sneak.
	
	public void onFall() { return; } // Quand un joueur tombe.
	
	public void onShoot(Projectile projectile) { return; } // Quand le joueur vient de tirer un projectile.
	
	public Double removeDamage(Double damage, Skylander skylanderHit) { return damage; } // On modifie les dégats.
	
	public Double addDamage(Double damage, Skylander skylanderHit) { return damage; } // On modifie les dégats.
	
	public Boolean onHitBow(Skylander skylanderDamager) { return false; } // Quand le joueur prend un dégat d'un projectile.
	
	public Boolean onHitSword(Skylander skylanderDamager) { return false; } // Quand le joueur prend un dégat d'une épée. 
	
	public Boolean onDamageBow(Skylander skylanderHit, Projectile projectile) { return false; } // Quand le joueur inflige un dégat avec un projectile. 
	
	public Boolean onDamageSword(Skylander skylanderHit) { return false; } // Quand le joueur inflige un dégat avec une épée.
	
	public Boolean onKill(Skylander skylanderDeath) { return false; } // Quand le joueur fait une élimination.
	
	public Boolean onDeath(Skylander skylanderKill) { return false; } // Quand le joueur vient de mourir.
	
	public Boolean applyEnemyResistance() { return true; } // Lorsque le joueur tape, on vérifie si on applique la résistance de l'ennemi.
	
	public Boolean applyEnemyStrenght() { return true; } // Lorsque le joueur se fait frappé, on vérifie si on applique la force de l'ennemi.
	
}
