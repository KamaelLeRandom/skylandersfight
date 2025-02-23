package fr.kamael.skylandersfight.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class GameListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void entityFight(EntityDamageByEntityEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Entity damager = event.getDamager();
				Entity entity = event.getEntity();
				
				if (damager instanceof Player && !(entity instanceof Player)) {
					Skylander skylander = plugin.game.getPlayer((Player) damager).getSkylander();
					CustomEntity customEntity = plugin.game.getRound().getArena().isCustomEntity(entity);
					
					if (customEntity != null && skylander != null) {
						customEntity.onHit(skylander);
					}
					
					return;
				}
				
				else if (entity instanceof Player && !(damager instanceof Player)) {
					Skylander skylander = plugin.game.getPlayer((Player) entity).getSkylander();
					CustomEntity customEntity = plugin.game.getRound().getArena().isCustomEntity(damager);
					
					if (customEntity != null && skylander != null) {
						customEntity.onDamage(skylander);
					}
					
					return;
				}
			}
			return;
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, entityFight) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				event.getDrops().clear();
				
				Entity entity = event.getEntity();
				CustomEntity customEntity = this.plugin.game.getRound().getArena().isCustomEntity(entity);
				
				if (customEntity != null) {
					customEntity.onDeath();
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, entityDeath) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerFight(EntityDamageByEntityEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {

				if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
					Projectile projectile = (Projectile) event.getDamager();
					
					if (projectile.getShooter() instanceof Player) {
						Player playerHit = (Player) event.getEntity();
						Player playerShooter = (Player) projectile.getShooter();
						Skylander skylanderHit = plugin.game.getPlayer(playerHit).getSkylander();
						Skylander skylanderShooter = plugin.game.getPlayer(playerShooter).getSkylander();
						Double damage = event.getDamage();
						
						// Première possible condition d'annulation. (Status, Mates)
						if (skylanderHit.checkStatus(Status.NOTAKEDAMAGE) || skylanderShooter.checkStatus(Status.NOMAKEDAMAGE) || skylanderShooter.getMates().contains(skylanderHit)) {
							event.setCancelled(true);
							return;
						}
						
						// Deuxième possible condition d'annulation. (onHit, onDamage)
						if (skylanderShooter.onDamageBow(skylanderShooter, projectile) || skylanderHit.onHitBow(skylanderShooter)) {
							event.setCancelled(true);
							return;
						}
						
						damage = skylanderShooter.addDamage(damage, skylanderHit);
						damage = skylanderHit.removeDamage(damage, skylanderShooter);
						
						if (skylanderHit.applyEnemyStrenght())
							damage *= skylanderShooter.getForce();
						
						if (skylanderShooter.applyEnemyResistance())
							damage *= skylanderHit.getResis();
						
						if (skylanderShooter.getElement().equals(Element.FEU))
							playerHit.setFireTicks(80);
						
						event.setDamage(damage);
						return;
					}
					
					return;
				}
				else if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
					Player playerHit = (Player) event.getEntity();
					Player playerDamager = (Player) event.getDamager();
					Skylander skylanderHit = plugin.game.getPlayer(playerHit).getSkylander();
					Skylander skylanderDamager = plugin.game.getPlayer(playerDamager).getSkylander();
					Double damage = event.getDamage();
					
					// Première possible condition d'annulation. (Status, Mates)
					if (skylanderHit.checkStatus(Status.NOTAKEDAMAGE) || skylanderDamager.checkStatus(Status.NOMAKEDAMAGE) || skylanderDamager.getMates().contains(skylanderHit)) {
						event.setCancelled(true);
						return;
					}
					
					// Deuxième possible condition d'annulation. (onHit, onDamage)
					if (skylanderDamager.onDamageSword(skylanderHit) || skylanderHit.onHitSword(skylanderDamager)) {
						event.setCancelled(true);
						return;
					}
					
					damage = skylanderDamager.addDamage(damage, skylanderHit);
					damage = skylanderHit.removeDamage(damage, skylanderDamager);
					
					if (skylanderHit.applyEnemyStrenght())
						damage *= skylanderDamager.getForce();
					
					if (skylanderDamager.applyEnemyResistance())
						damage *= skylanderHit.getResis();
					
					if (skylanderDamager.getElement().equals(Element.FEU))
						playerHit.setFireTicks(80);
					
					event.setDamage(damage);
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerProjectileLaunch) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerDamage(EntityDamageEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING) && event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				Skylander skylander = plugin.game.getPlayer(player).getSkylander();

				if (
					skylander.checkStatus(Status.NOTAKEDAMAGE) ||
					event.getCause().equals(DamageCause.LIGHTNING)
				) {
					event.setCancelled(true);
					return;
				}

				if (event.getCause().equals(DamageCause.FALL)) {
					skylander.onFall();
					
					if (skylander.checkStatus(Status.NOFALL)) {
						event.setCancelled(true);
					} else if (skylander.checkStatus(Status.ONEFALL)) {
						event.setCancelled(true);
						skylander.removeStatus(Status.ONEFALL);
					} else {
						event.setDamage(event.getDamage() / 2.);
					}
					
					return;
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerProjectileLaunch) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Player playerDeath = event.getEntity();
				Skylander skylanderDeath = this.plugin.game.getPlayer(playerDeath).getSkylander();
				Location locationDeath = playerDeath.getLocation().clone();

				if (playerDeath.getKiller() instanceof Player) {
					Player playerKiller = playerDeath.getKiller();
					Skylander skylanderKiller = this.plugin.game.getPlayer(playerKiller).getSkylander();
					
					if (skylanderDeath.onDeath(skylanderKiller))
						return;
					
					skylanderKiller.onKill(skylanderDeath);
					
					SpellUtils.heal(skylanderKiller, 20.);
					
					event.setDeathMessage(Constants.prefixMessage + "§e§l" + playerDeath.getName() + "§f§r (§7" + skylanderDeath.getName() + "§f) a été éliminé par §e§l"+ playerKiller.getName() +"§f§r.");
				} else {
					if (skylanderDeath.onDeath(null))
						return;
					
					event.setDeathMessage(Constants.prefixMessage + "§e§l" + playerDeath.getName() + "§f§r (§7" + skylanderDeath.getName() + "§f) est mort.");
				}

				ItemManager.clearPlayer(playerDeath);
				skylanderDeath.setAlive(false);
				playerDeath.spigot().respawn();
				playerDeath.teleport(locationDeath);
				playerDeath.setGameMode(GameMode.SPECTATOR);

				plugin.game.getRound().getArena().summonArenaCorpse(skylanderDeath, locationDeath);
				plugin.game.getRound().checkVictory();
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerDeath) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerProjectileLaunch(ProjectileLaunchEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Projectile projectile = event.getEntity();
				
				if (projectile.getShooter() instanceof Player) {
					Skylander skylander = this.plugin.game.getPlayer((Player) projectile.getShooter()).getSkylander();
					
					if (skylander != null) {
						skylander.onShoot(projectile);
					}
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerProjectileLaunch) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Player player = event.getPlayer();
				Skylander skylander = this.plugin.game.getPlayer(player).getSkylander();
	            Location from = event.getFrom();
	            Location to = event.getTo();
	            
	            if (skylander.checkStatus(Status.NOMOVE) && (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())) {
					event.setCancelled(true);
				}
				
				if (skylander.isAlive() && player.getLocation().getY() <= 0.) {
					skylander.addStatus(null, Status.ONEFALL);
					player.teleport(plugin.game.getRound().getArena().getRandomPlayerSpawn());
					player.damage(5);
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerMove) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerToggleSneak(PlayerToggleSneakEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Player player = event.getPlayer();
				Skylander skylander = this.plugin.game.getPlayer(player).getSkylander();
				
				if (skylander != null) {
					skylander.onSneak();
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerToggleSneak) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerTeleport(PlayerTeleportEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Player player = event.getPlayer();
				Skylander skylander = this.plugin.game.getPlayer(player).getSkylander();
				
				if (skylander.isAlive() && player.getGameMode().equals(GameMode.SPECTATOR) && event.getCause().equals(TeleportCause.SPECTATE)) {
					event.setCancelled(true);
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerTeleport) : §7"+e.getMessage());	
			return;
		}
	}
	
	@EventHandler
	public void playerInteractEmeraldBlock(PlayerInteractEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {
				Block clickedBlock = event.getClickedBlock();
				Player player = event.getPlayer();
				Skylander skylander = this.plugin.game.getPlayer(player).getSkylander();
				
				if (skylander.isAlive() && player.getGameMode().equals(GameMode.ADVENTURE) && clickedBlock != null && clickedBlock.getType().equals(Material.EMERALD_BLOCK)) {
				
					if (this.plugin.game.getConfig().getActiveHeal()) {
						SpellUtils.heal(skylander, Constants.valueHeal);
						
						clickedBlock.setType(Material.BEDROCK);
						
						new BukkitRunnable() {
							private Integer timer = plugin.random.nextInt(30) + 30;
							
							@Override
							public void run() {
								if (timer == 0 || !plugin.game.isState(GameState.FIGHTING)) {
									if (timer == 0) {
										Bukkit.broadcastMessage(Constants.prefixMessage + "Un §6Soin§f est réapparu !");
									}
									clickedBlock.setType(Material.EMERALD_BLOCK);
									cancel();
									return;
								}
								
								timer--;
							}
						}.runTaskTimer(plugin, 0, 20);
					}
					else {
						player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Les soins ont été §cdésactivés§f.");
						clickedBlock.setType(Material.BEDROCK);
					}
				}
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(GameListener, playerInteractEmeraldBlock) : §7"+e.getMessage());	
			return;
		}
	}
}
