package fr.kamael.skylandersfight.skylanders.mort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.mort.entity.FrightRiderHorse;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class FrightRider extends Skylander {
	public static final String name = "Fright Rider";
	
	public static final String nameWeapon = "§7Lance";
	public static final String namePassif = "§7Ozzy";
	public static final Integer timerPassif = 15;
	public static final Double resisPassif = 0.25;
	
	public static final String nameFirstSpell = "§7Assaut Cavalerie";
	public static final Integer timerFirstSpell = 15;
	public static final Double vectorPowerFirstSpell = 1.2;
	public static final Double rangeFirstSpell = 1.5;
	public static final Double damageFirstSpell = 6.;
	public static final Integer tickRootFirstSpell = 40;
	
	public static final String nameSecondSpell = "§7Camouflage Néfaste";
	public static final Integer timerSecondSpell = 30;
	public static final Integer tickInvisibilitySecondSpell = 100;

	private FrightRiderHorse ozzy = null; 
	
	public FrightRider(Player player) {
		super(player, Element.MORT, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(8, getItemPassif());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	@Override
	public void onSneak() {
		if (ozzy != null) {
			ozzy.removeEntity();
			removePassif();
			addCooldown(namePassif, timerPassif);
		}

		return; 
	}
	
	@Override
	public void onShoot(Projectile projectile) {
		if (projectile instanceof Trident trident) {			
			new BukkitRunnable() {
				private Integer timer = 20;
				@Override
				public void run() {
					if (timer == 0) {
						player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1, 1);
						player.getInventory().addItem(trident.getItem());
						trident.remove();
					}
					
					if (player.getInventory().contains(Material.TRIDENT) || timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 10);
		}

		return; 
	}
	
	public void passif_Horse() {
		if (checkCooldown(namePassif, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_HORSE_SADDLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'invoquer votre "+ namePassif +"§f.");
			ozzy = new FrightRiderHorse(this, player.getLocation());
			resis -= resisPassif;
		}
	}
	
	public void removePassif() {
		ozzy = null;
		resis += resisPassif;
	}
	
	public void firstSpell_Dash() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			
			if (ozzy == null) {
				SpellUtils.dash(
					this, 
					player,
					vectorPowerFirstSpell, 
					rangeFirstSpell, 
					15, 
					(attacker, target) -> {
						Player playerTarget = target.getPlayer();
						playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
						playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell + "§f de §3" + player.getName() + "§f.");
						playerTarget.damage(damageFirstSpell, player);
					}, 
					(location) -> {
					    Material block = Material.BONE_BLOCK;
					    location.getWorld().spawnParticle(
					        Particle.BLOCK_CRACK,
					        location,
					        20,
					        0.4, 0.1, 0.4,
					        0.1,
					        block.createBlockData()
					    );
					    
					    location.getWorld().spawnParticle(
					    	Particle.CLOUD,
					        location,
					        10,
					        0.3, 0.1, 0.3,
					        0.05
					    );
					}
				);
			} else {
				SpellUtils.dash(
						this, 
						ozzy.getEntity(),
						vectorPowerFirstSpell * 2, 
						rangeFirstSpell, 
						15, 
						(attacker, target) -> {
							Player playerTarget = target.getPlayer();
							playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
							playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell + "§f de §3" + player.getName() + "§f.");
							playerTarget.damage(damageFirstSpell, player);
							playerTarget.sendTitle(nameFirstSpell, "§7Immobilisation de "+ SkylanderConverter.convertTicks(tickRootFirstSpell) + "s.", 1, tickRootFirstSpell, 1);
							target.addStatus(tickRootFirstSpell, Status.NOMOVE);
						}, 
						(location) -> {
						    Material block = Material.BONE_BLOCK;
						    location.getWorld().spawnParticle(
						        Particle.BLOCK_CRACK,
						        location,
						        20,
						        0.4, 0.1, 0.4,
						        0.1,
						        block.createBlockData()
						    );
						    
						    location.getWorld().spawnParticle(
						    	Particle.CLOUD,
						        location,
						        10,
						        0.3, 0.1, 0.3,
						        0.05
						    );
						}
					);
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Invisibility() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			
			if (ozzy != null) {
				ozzy.invisibility();
				SpellUtils.invisibility(plugin, this, tickInvisibilitySecondSpell * 2);
			} else {
				SpellUtils.invisibility(plugin, this, tickInvisibilitySecondSpell);
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, vous pouvez montez sur votre cheval, lorsque vous êtes dessus vous gagnez §3"+ resisPassif*100 +"%§f de §cRésistance§f supplémentaires.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous effectuez une §3ruée§f vers l'avant qui inflige §3"+ damageFirstSpell +" dégats§f aux joueurs sur votre passage, si vous êtes sur "+ namePassif +"§f les joueurs seront §3immobilisés§f pendant "+ SkylanderConverter.convertTicks(tickRootFirstSpell) +" secondes. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous devenez invisible pendant "+ SkylanderConverter.convertTicks(tickInvisibilitySecondSpell) +" secondes, cette durée est doublé si vous êtes sur "+ namePassif +"§f. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7"+ name +"§f est un Skylander §cmélée§f capable");
		lore.add("§fd'invoquer un fidèle destrier.");
		ItemStack item = new ItemStack(Material.SADDLE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous effectuez une ruée vers l'avant qui inflige "+ damageFirstSpell +" dégats aux joueurs sur votre passage.",
			"§fSi vous êtes sur " + namePassif + "§f, les joueurs seront immobilisé pendant " + SkylanderConverter.convertTicks(tickRootFirstSpell) + "§f."
		);
		ItemStack item = new ItemStack(Material.STICK, 1);
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
		List<String> lore = Arrays.asList(
			"§fVous devenez invisible pendant "+ SkylanderConverter.convertTicks(tickInvisibilitySecondSpell) +"s, cette durée est doublé si vous êtes sur "+ namePassif +"."
		);
		ItemStack item = new ItemStack(Material.PHANTOM_MEMBRANE, 1);
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
	
	public static ItemStack getItemPassif() {
		List<String> lore = Arrays.asList(
			"§fVous pouvez monter sur votre cheval " + namePassif + "."
		);
		ItemStack item = new ItemStack(Material.SADDLE, 1);
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
	
	public static ItemStack getItemWeapon() {
		ItemStack item = new ItemStack(Material.TRIDENT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
}
