package fr.kamael.skylandersfight.skylanders.vie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Camo extends Skylander {
	public static final String name = "Camo";
	
	public static final String nameWeapon = "§2Lance-Pastèque";
	public static final String namePassif = "§2Recolte de Pastèque";
	public static final Double healPassif = 1.;
	public static final Double probaGoldPassif = 0.10;
	public static final Double bonusGoldPassif = 0.02;
	public static final Integer numberMaxItemPassif = 3;
	public static final Integer numberMinItemPassif = 1;
	
	public static final String nameFirstSpell = "§2Liane Empoisonnée";
	public static final Integer timerFirstSpell = 20;
	public static final Double damageFirstSpell = 5.;
	public static final Integer secDurationPoisonFirstSpell = 8;
	public static final Integer distanceFirstSpell = 15;
	public static final Double rangeFistSpell = 0.8;
	
	public static final String nameSecondSpell = "§2Explosion Fruité";
	public static final Integer timerSecondSpell = 30;
	public static final Double rangeSecondSpell = 3.;
	public static final Double damageSecondSpell = 4.;
	
	private ArrayList<Item> itemsDrop = new ArrayList<Item>(); 
	
	public Camo(Player player) {
		super(player, Element.VIE, name);
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
	
	@Override
	public Boolean onDeath(Skylander skylanderKill) {
		for (Item itemDrop : itemsDrop) {
			if (!itemDrop.isDead()) {
				itemDrop.remove();
			}
		}
		
		return false; 
	}
	
	@Override
	public Boolean onPickupItem(Item item) {
		Material material = item.getItemStack().getType();
		
		if (material.equals(Material.MELON_SLICE)) {
			SpellUtils.heal(this, healPassif, true);
			item.remove();
		} else if (material.equals(Material.GLISTERING_MELON_SLICE)) {
			SpellUtils.heal(this, healPassif, true);
			force += bonusGoldPassif;
			resis -= bonusGoldPassif;
			item.remove();
		}
		
		return true; 
	}
	
	@Override
	public Boolean onDamageBow(Skylander skylanderHit, Projectile projectile) { 
	    Player playerHit = skylanderHit.getPlayer();
	    Location location = playerHit.getLocation();

	    passif_Drop(location);
	    
	    return false; 
	}
	
	private void passif_Drop(Location location) {
	    Integer amount = plugin.random.nextInt(numberMaxItemPassif) + numberMinItemPassif;

	    for (int i = 0; i < amount; i++) {
	        Material material =  plugin.random.nextDouble() < probaGoldPassif ? Material.GLISTERING_MELON_SLICE : Material.MELON_SLICE;
	        ItemStack melon = new ItemStack(material, 1);
	        ItemMeta meta = melon.getItemMeta();
	        meta.setDisplayName("Melon #" + UUID.randomUUID().toString().substring(0, 8));
	        melon.setItemMeta(meta);
	        itemsDrop.add(location.getWorld().dropItemNaturally(location, melon));
	    }
	}
	
	public void firstSpell_Poison() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				distanceFirstSpell, 
				rangeFistSpell, 
				(location) -> {
			        World world = location.getWorld();

			        world.spawnParticle(
			            Particle.BLOCK_CRACK,
			            location,
			            10,
			            0.2, 0.1, 0.2,
			            0.05,
			            Material.VINE.createBlockData()
			        );

			        world.spawnParticle(
			            Particle.REDSTONE,
			            location,
			            15,
			            0.3, 0.1, 0.3,
			            0,
			            new Particle.DustOptions(Color.fromRGB(50, 200, 80), 1.2f)
			        );

			        world.spawnParticle(
			            Particle.SPELL_MOB,
			            location,
			            8,
			            0.2, 0.1, 0.2,
			            0
			        );
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f, cependant §cpersonne§f n'a été touché.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_CAVE_VINES_PICK_BERRIES, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell +"§f de §a"+ player.getName() +"§f.");
				playerTarget.damage(damageFirstSpell, player);
				playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.POISON, secDurationPoisonFirstSpell * 20, 0, false, false));
				playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, secDurationPoisonFirstSpell * 20, 2, false, false));

				player.playSound(player.getLocation(), Sound.BLOCK_CAVE_VINES_PICK_BERRIES, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §a"+ playerTarget.getName() +"§f.");
			}
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_Explosion() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			
			ArrayList<Skylander> skylandersHit = new ArrayList<Skylander>();
			
			for (Item itemDrop : itemsDrop) {
				for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, itemDrop.getLocation(), rangeSecondSpell, 2., rangeSecondSpell)) {
					if (!skylandersHit.contains(skylanderHit)) {
						Player playerHit = skylanderHit.getPlayer();
						playerHit.playSound(playerHit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
						playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameSecondSpell +"§f de §a"+ player.getName() +"§f.");
						playerHit.damage(damageSecondSpell, player);
						
						skylandersHit.add(skylanderHit);
					}
				}
				
				itemDrop.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, itemDrop.getLocation(), 1);
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, lorsque vous touchez une flèche sur un joueur, un montant entre "+ numberMinItemPassif +" et "+ numberMaxItemPassif +" de Pastèque vont tomber au sol, lorsque vous les ramassez vous êtes §asoigné de "+ healPassif +" point de vie§f. Il y a §6"+ probaGoldPassif*100 +"%§f que la §ePastèque soit dorée§f ce qui vous donne en plus §6"+ bonusGoldPassif*100 +"%§f de §cForce§f et §cRésistance§f.");
	    player.sendMessage("\n"); 
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous envoyez en face de vous des lianes ("+ distanceFirstSpell +" blocs) qui inflige §3"+ damageFirstSpell +" dégats§f, §aempoisonne§f et §7ralentif§f pendant §6"+ secDurationPoisonFirstSpell +" secondes§f. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous faites exploser tout les pastèques qui sont au sol, infligeant "+ damageSecondSpell +" aux joueurs proche. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§2"+ name +"§f est un Skylander §cdistance§f capable de soigner");
		lore.add("§fen récupérant des pastèques au sol.");
		ItemStack item = new ItemStack(Material.MELON_SLICE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§2"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§f."
		);
		ItemStack item = new ItemStack(Material.VINE, 1);
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
			"§f."
		);
		ItemStack item = new ItemStack(Material.GUNPOWDER, 1);
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
		return item;
	}
}
