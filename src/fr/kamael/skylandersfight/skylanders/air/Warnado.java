package fr.kamael.skylandersfight.skylanders.air;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Warnado extends Skylander {
	public static final String name = "Warnado";
	
	public static final String nameWeapon = "§3Piques";
	
	public static final String namePassif = "§3Roulade";
	public static final Integer nbMaxPassif = 2;
	public static final Double damagePassif = 3.;
	
	public static final String nameFirstSpell = "§3Tempête";
	public static final Integer timerFirstSpell = 20;
	public static final Double rangeFirstSpell = 5.;
	public static final Double powerVectorFirstSpell = 1.5;
	
	public static final String nameSecondSpell = "§3Carapace";
	public static final Integer timerSecondSpell = 20;
	public static final Integer durationSecondSpell = 3;
	public static final Integer tickStunSecondSpell = 50;
	public static final Double damageSecondSpell = 5.;
	
	private Boolean isActivePassif = false;
	private Integer nbDashPassif = 0;
	
	public Warnado(Player player) {
		super(player, Element.AIR, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.WHITE);
						
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}

	public Boolean onHitSword(Skylander skylanderDamager) {
		if (isActivePassif) {
			isActivePassif = false;
			
			Player playerDamager = skylanderDamager.getPlayer();
			
			playerDamager.playSound(playerDamager.getLocation(), Sound.ENTITY_TURTLE_HURT, 1, 1);
			playerDamager.sendTitle(nameSecondSpell, "Vous êtes étourdi pendant "+ SkylanderConverter.convertTicks(tickStunSecondSpell) +"s.", 1, tickStunSecondSpell, 1);
			playerDamager.sendMessage(Constants.prefixMessage + "Vous venez de subir le contre-coup de la compétence "+ nameSecondSpell +"§f de §3"+ player.getName() +"§f.");
			playerDamager.damage(damageSecondSpell, player);
			
			skylanderDamager.addStatus(tickStunSecondSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
		}
		
		if (nbDashPassif+1 <= nbMaxPassif) {
			nbDashPassif++;
			
			player.setLevel(nbDashPassif);
		}
		
		return false; 
	}
	
	public void passif_Dash() {
		if (nbDashPassif-1 >= 0) {
			nbDashPassif--;
			player.setLevel(nbDashPassif);
			
			SpellUtils.dash(
				this, 
				powerVectorFirstSpell, 
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.damage(damagePassif);
					playerTarget.setVelocity(new Vector(0, 1.0, 0));
				}, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
					world.spawnParticle(Particle.SMOKE_NORMAL, location, 15, 0.3, 0.3, 0.3);
				}
			);
		}
	}
	
	public void firstSpell_Tempest() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeFirstSpell, 2., rangeFirstSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				
				SpellUtils.tornado(plugin, skylanderHit);
				ParticleUtils.tornadoParticule(playerHit.getLocation(), Particle.SMOKE_NORMAL);
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
		}
	}
	
	public void secondSpell_Counter() {
		if (checkCooldown(nameSecondSpell, true)) {
			ItemStack helmet = player.getEquipment().getHelmet().clone();
			
			player.getEquipment().getHelmet().setType(Material.TURTLE_HELMET);
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			isActivePassif = true;
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpell;
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING) || !isActivePassif) {
						player.getEquipment().setHelmet(helmet);
						isActivePassif = false;
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§3" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, à chaque fois que vous subissez un dégat au corps à corps, vous gagnez une §eruée vers l'avant§f qui inflige §e"+ damagePassif +" dégats§f et envoie les joueurs en l'air. (cummulable "+ nbMaxPassif +" fois à la fois)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous invoquez une tornade qui propulse en l'air les joueurs autour de vous ("+ rangeFirstSpell +" blocs) . §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, pendant "+ durationSecondSpell +" secondes, si un joueur vous frappe, vous l'§eétourdissez§f pendant "+ SkylanderConverter.convertTicks(tickStunSecondSpell) +" et lui ingligez §e"+ damageSecondSpell +" dégats§f. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§3"+ name +"§f est un Skylander §cmélée§f");
		lore.add("§ftrès mobile et possèdant un puissant contre.");
		
		ItemStack item = new ItemStack(Material.TURTLE_HELMET, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous envoyez en l'air le joueur ciblé.");
		
		ItemStack item = new ItemStack(Material.STRING, 1);
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
		List<String> lore = Arrays.asList("§fVous équipez un casque de tortue pendant "+ durationSecondSpell +"s, si un joueur vous frappe a ce moment", "§fil sera étourdissement et prendra de lourd dégats.");
	
		ItemStack item = new ItemStack(Material.TURTLE_HELMET, 1);
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
		ItemStack item = new ItemStack(Material.GOLDEN_SWORD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3Piques");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		return item;
	}
}
