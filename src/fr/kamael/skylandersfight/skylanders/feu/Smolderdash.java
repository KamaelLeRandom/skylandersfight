package fr.kamael.skylandersfight.skylanders.feu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.feu.entity.SmolderdashFireball;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Smolderdash extends Skylander {
	public static final String name = "Smolderdash";
	
	public static final String nameWeapon = "§4Fouet Enflammé";
	
	public static final String namePassif = "§4Propulsion";
	public static final Integer timerPassif = 5;
	
	public static final String nameFirstSpell = "§4Soleil";
	public static final Integer timerFirstSpell = 30;
	public static final Integer tickFireballFirstSpell = 300;
	public static final Double bonusDamageFireballFirstSpell = 0.05;
	
	public static final String nameSecondSpell = "§4Combustion";
	public static final Integer timerSecondSpell = 30;
	public static final Double bonusForceSecondSpell = 0.2;
	public static final Double autoDamageSecondSpell = 1.;
	public static final Integer tickAutoDamageSecondSpell = 15;
	
	private Boolean isSecondSpellActive = false;
	
	public Smolderdash(Player player) {
		super(player, Element.FEU, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.RED);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void passif_Jump(Location hookLocation) {
		if (checkCooldown(namePassif, false)) {
		    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 100, 0), 1.5f);
		    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.1, 0), 15, 0.3, 0.1, 0.3, dust);
		    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.1, 0), 20, 0.3, 0.1, 0.3, 0.02);
		    player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation().add(0, 0.1, 0), 5, 0.2, 0.1, 0.2, 0.05);
			
	        Location playerLocation = player.getLocation();
	        Vector direction = hookLocation.toVector().subtract(playerLocation.toVector()).normalize();
	        direction.multiply(1.8);
	        direction.setY(0.5);
	        player.setVelocity(direction);
	        
	        addStatus(null, Status.ONEFALL);
			addCooldown(namePassif, timerPassif);
		}
	}
	
	public void firstSpell_Fireball() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utliser votre compétence " + nameFirstSpell + "§f.");
			
			new SmolderdashFireball(this, player.getEyeLocation().clone().add(0, 1, 0));
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
 	public void secondSpell_Powerup() {
		if (isSecondSpellActive) {
			isSecondSpellActive = false;
			return;
		}
		
		if (checkCooldown(nameSecondSpell, true)) {
			isSecondSpellActive = true;
			
			player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'§aactiver§f votre compétence " + nameSecondSpell + "§f.");
			force += bonusForceSecondSpell;
			
			new BukkitRunnable() {
				private Integer timer = 0;
				
				@Override
				public void run() {
					if (!isSecondSpellActive || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						
				        for (int i = 0; i < 10; i++) {
				            double xOffset = (plugin.random.nextDouble() * 2 - 1) * 1.2;
				            double yOffset = (plugin.random.nextDouble() * 2);            
				            double zOffset = (plugin.random.nextDouble() * 2 - 1) * 1.2;

				            Location particleLocation =  player.getLocation().clone().add(xOffset, yOffset, zOffset);

				            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLocation, 0, 0, 0, 0, 0);
				            if (plugin.random.nextDouble() < 0.2) {
				                player.getWorld().spawnParticle(Particle.WATER_SPLASH, particleLocation, 0, 0, 0, 0, 0);
				            }
				        }
						
						player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Vous venez de §cdésactiver§f votre compétence " + nameSecondSpell + "§f.");
						force -= bonusForceSecondSpell;
						
						addCooldown(nameSecondSpell, timerSecondSpell);
						
						cancel();
						return;
					}
					
					if (timer % tickAutoDamageSecondSpell == 0) {
						if (player.getHealth() - autoDamageSecondSpell > 0) {
							player.setHealth(player.getHealth() - autoDamageSecondSpell);
						} else {
							isSecondSpellActive = false;
						}
						
				        for (int i = 0; i < 10; i++) {
				            double xOffset = (plugin.random.nextDouble() * 2 - 1) * 1.2;
				            double yOffset = (plugin.random.nextDouble() * 2);            
				            double zOffset = (plugin.random.nextDouble() * 2 - 1) * 1.2;

				            Location particleLocation =  player.getLocation().clone().add(xOffset, yOffset, zOffset);

				            player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 0, 0, 0, 0, 0);
				            if (plugin.random.nextDouble() < 0.2) {
				                player.getWorld().spawnParticle(Particle.LAVA, particleLocation, 0, 0, 0, 0, 0);
				            }
				        }
					}

					timer++;
				}
			}.runTaskTimer(plugin, 0, 1);
			
			return;
		}
	}
 	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶" + element.getColor() + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ §6" + namePassif + "§f, vous pouvez utiliser votre " + nameWeapon + "§f afin d'être vers la direction ou vous l'avez lancé. §b(" + timerPassif + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, Vous gagnez §6" + bonusForceSecondSpell*100 + "% de Force§f supplémentaire, cependant vous subissez §c" + autoDamageSecondSpell + "§f toutes les §c" + SkylanderConverter.convertTicks(tickAutoDamageSecondSpell) + " secondes§f, pour arrêter cette compétence vous devez réintéragir avec l'objet. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
 	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§4"+ name +"§f est un Skylander §cmélée§f possèdant");
		lore.add("§fune grande mobilité et puissance.");
		
		ItemStack item = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Element.FEU.getColor() + name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§f.");
		
		ItemStack item = new ItemStack(Material.FIRE_CHARGE);
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
		List<String> lore = Arrays.asList("§fVous gagnez §6" + bonusForceSecondSpell*100 + "% de Force§f supplémentaire,", "vous §6subissez " + autoDamageSecondSpell + " dégats§f toutes les §6" + SkylanderConverter.convertTicks(tickAutoDamageSecondSpell) + " secondes§f.");
		
		ItemStack item = new ItemStack(Material.BLAZE_POWDER, 1);
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
		ItemStack item = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 8);

		return item;
	}
}
