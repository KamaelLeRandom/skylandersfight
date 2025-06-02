package fr.kamael.skylandersfight.skylanders.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class TriggerHappy extends Skylander {
	public static final String name = "Trigger Happy";
	
	public static final String namePassif = "§eOr brut";
	public static final Integer damagePassif = 1;
	
	public static final String nameFirstSpell = "§eMitrailette dorée";
	public static final Integer timerFirstSpell = 30;
	public static final Integer nbArrowFirstSpell = 10;
	public static final Integer tickArrowFirstSpell = 15;
	public static final Double damageArrowFirstSpell = 1.5;
	
	public static final String nameSecondSpell = "§eBaril d'or";
	public static final Integer timerSecondSpell = 30;
	public static final Integer timeStunSecondSpell = 50;
	
	private Integer damageBonusPassif = 0;
	
	public TriggerHappy(Player player) {
		super(player, Element.TECH, name);
		this.force = 1.05;
		this.resis = 0.95;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Boolean onDamageBow(Skylander skylanderHit, Projectile projectile) { 
		if (projectile instanceof Arrow) {
			player.getInventory().addItem(getItemPassif());
		}
		
		return false; 
	}
	
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (damageBonusPassif == 0) {
			return damage;
		} else {
			Double newDamage = damage + damageBonusPassif;
			damageBonusPassif = 0;
			return newDamage;
		}
	}
	
	public void passif_Gold() {
		for (ItemStack item : player.getInventory().all(Material.GOLD_INGOT).values()) {
			damageBonusPassif += item.getAmount();
			item.setAmount(0);
		}
		
		player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_MIDDLE, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre "+ namePassif + "§f, la prochaine flèche que vous toucherez infligera §e"+ damageBonusPassif + " dégats§f bonus.");
	}
	
	public void firstSpell_Arrow() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser la compétence "+ nameFirstSpell +"§f.");

			new BukkitRunnable() {
				private Integer time = nbArrowFirstSpell;
				
				@Override
				public void run() {
					if (time <= 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					Location arrowSpawnLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.2));
					Vector arrowVelocity = player.getEyeLocation().getDirection().normalize().multiply(2);
					Arrow arrow = (Arrow) player.getWorld().spawnEntity(arrowSpawnLocation, EntityType.ARROW);
					arrow.setVelocity(arrowVelocity);
					arrow.setDamage(damageArrowFirstSpell);
					arrow.setShooter(player);
					arrow.setKnockbackStrength(0);
										
					time--;
				}
			}.runTaskTimer(plugin, 0, tickArrowFirstSpell);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Stun() {
		if (checkCooldown(nameSecondSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				15,
				0.75,
				(location) -> {
			        location.getWorld().spawnParticle(
			        	Particle.BLOCK_CRACK, 
			            location, 
			            2, 
			            0.05, 0.05, 0.05,
			            0,
			            Material.GOLD_BLOCK.createBlockData() // Type de bloc
			        );
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
				return;
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_BREAK, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Vous avez été touché par la compétence "+ nameSecondSpell + "§f de §6"+ player.getName() + "§f.");
				playerTarget.sendTitle(nameSecondSpell, "§7Étourdissement de §e"+ SkylanderConverter.convertTicks(timeStunSecondSpell) +"s§f.", 2, timeStunSecondSpell, 2);
				skylanderTarget.addStatus(timeStunSecondSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
				
				player.playSound(player.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_BREAK, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser la compétence "+ nameSecondSpell +"§f sur §6"+ playerTarget.getName() +"§f.");
				
		        ArrayList<Entity> droppedItems = new ArrayList<Entity>();

		        for (int i = 0; i < 10; i++) {
		            double offsetX = (plugin.random.nextDouble() * 2 - 1) * 0.8;
		            double offsetZ = (plugin.random.nextDouble() * 2 - 1) * 0.8;
		            Location dropLocation = playerTarget.getLocation().clone().add(offsetX, 1, offsetZ);

		            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 1);
		            ItemMeta meta = goldIngot.getItemMeta();
		            meta.setDisplayName("gold_ingot_"+i);
		            goldIngot.setItemMeta(meta);
		            Item item = player.getWorld().dropItem(dropLocation, goldIngot);
		            droppedItems.add(item);
		        }

		        new BukkitRunnable() {
		            @Override
		            public void run() {
		                for (Entity entity : droppedItems) {
		                    if (!entity.isDead()) {
		                        entity.remove();
		                    }
		                }
		                cancel();
		                return;
		            }
		        }.runTaskLater(plugin, 50);
				
				addCooldown(nameSecondSpell, timerSecondSpell);
				return;
			}
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶§eTrigger Happy§f◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, vous pouvez §6augmenter les dégâts§f de votre prochaine flèche pour un montant de §6"+ damagePassif +" * votre nombre d'or§f utilisé.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous §6tirez automatiquement§f une §6flèche§f toutes "+ SkylanderConverter.convertTicks(tickArrowFirstSpell) +" secondes pour un total de §6"+ nbArrowFirstSpell +" fois§f. §b("+ timerFirstSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous §6étourdissez§f le §6joueur ciblé§f pendant "+ SkylanderConverter.convertTicks(timeStunSecondSpell) +" secondes. §b("+ timerSecondSpell +"s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§e"+ name +"§f est un Skylander à §cdistance§f §7§n(arc)§f§r");
		lore.add("§fcummulant des ressources afin d'infliger un gros burst.");
		
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§e"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemPassif() {
		List<String> lore = Arrays.asList("§fLorsque vous utilisez votre " + namePassif , "§fvous augmentez les dégats de votre prochaine flèche", "§fde §e"+ damagePassif + " * nombre d'or§f.");
		
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(namePassif);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}

	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous tirez §6automatique une flèche§f", "§ftoutes les "+ SkylanderConverter.convertTicks(tickArrowFirstSpell) +"s", "§fpour un total de §6"+ nbArrowFirstSpell + " fois§f.");
		
		ItemStack item = new ItemStack(Material.GOLD_NUGGET, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameFirstSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemSecondSpell() {
		List<String> lore = Arrays.asList("§fVous §6étourdissez§f le joueur ciblé", "§fpendant §6"+ SkylanderConverter.convertTicks(timeStunSecondSpell) +"s§f.");
		
		ItemStack item = new ItemStack(Material.GOLD_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpell);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemWeapon() {
		ItemStack item = new ItemStack(Material.BOW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§ePistolet");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		
		return item;
	}
}
