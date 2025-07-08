package fr.kamael.skylandersfight.skylanders.magie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Spyro extends Skylander {
	public static final String name = "Spyro";
	public static final String nameWeapon = "§5Tri-flamme";
	
	public static final String nameFirstSpell = "§5Percée Cornue";
	public static final Integer timerFirstSpell = 15;
	public static final Double damageFirstSpell = 3.;
	public static final Double vectorPowerFirstSpell = 2.;
	
	public static final String nameSecondSpell = "§5Vol Draconique";
	public static final Integer timerSecondSpell = 15;
	public static final Double damageSecondSpell = 3.;
	public static final Double rangeSecondSpell = 3.;
	
	private Boolean isSecondSpellActived = false;
	private Integer numberFireballSecondSpell = 0;
	
 	public Spyro(Player player) {
		super(player, Element.MAGIE, name);
		this.force = 1. + Element.magieForce;
	}
	
	@Override
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	@Override
	public void onShoot(Projectile projectile) {
		if (projectile instanceof Arrow) {
			Arrow arrow = (Arrow) projectile;
			
			for (Integer i = -1; i <= 1; i += 2) {
				Arrow arrow1 = player.getWorld().spawn(player.getEyeLocation(), Arrow.class);
				arrow1.setDamage(arrow.getDamage());
				arrow1.setKnockbackStrength(arrow.getKnockbackStrength());
				arrow1.setShooter(player);
				arrow1.setFireTicks(300);
				arrow1.setVelocity(arrow.getVelocity().rotateAroundY(Math.toRadians(i * 3)));
			}
		}
	}
	
	public void firstSpell_Dash() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Venez d'utiliser la compétence "+ nameFirstSpell +"§f.");
			
			SpellUtils.dash(
				this, 
				player,
				vectorPowerFirstSpell,
				2.5,
				20,
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.damage(damageFirstSpell);
					playerTarget.setFireTicks(5 * 20);
				}, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
					world.spawnParticle(Particle.SMALL_FLAME, location, 15, 0.3, 0.3, 0.3);
				}
			);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Fly() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			
			SpellUtils.fly(
				this, 
				rangeSecondSpell, 
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.damage(damageSecondSpell);
					playerTarget.setFireTicks(5 * 20);
				}, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
					world.spawnParticle(Particle.SMALL_FLAME, location, 15, 0.3, 0.3, 0.3);
				}
			);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ " + element.getColor() + Spyro.name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameWeapon +", vous tirez §dtrois flèches§f au lieu d'une.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous effectuez une §druée§f vers l'avant qui inflige §d" + Spyro.damageFirstSpell + " dégats§f et §denflamme§f les joueurs sur votre passage. §b(" + Spyro.timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous êtes §dpropulsé§f dans les airs, vous infligez §d" + Spyro.damageSecondSpell + " dégats§f aux joueurs sur votre passage. §b(" + Spyro.timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§5"+ name +"§f est un Skylander à §cdistance§f très mobile");
		lore.add("§fet maitrisant des armes enflammées.");
		
		ItemStack item = new ItemStack(Material.BLAZE_POWDER, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§5"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList("§fVous effectuez une §5ruée§f vers l'avant", "§finfligeant §d"+ damageFirstSpell +" dégats§f aux joueurs" , "§fsur votre passage.");
		
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
		List<String> lore = Arrays.asList("§fVous êtes envoyé dans les §dair§f", "§favec des §dElytra§f en infligeant §d"+ damageSecondSpell +" dégats", "§faux joueurs sur votre passage.");
		
		ItemStack item = new ItemStack(Material.FEATHER, 1);
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
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
		return item;
	}
}
