package fr.kamael.skylandersfight.skylanders.bogda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
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
import fr.kamael.skylandersfight.skylanders.bogda.utils.LeRosatasChaqklaInfo;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class LeRosatas extends Skylander {
	public static final String name = "LeRosatas";
	
	public static final String nameWeapon = "§dIssou";
	public static final String namePassif = "§dIssou";
	public static final Integer durationPassif = 30;
	
	public static final String nameFirstSpell = "§dChaqkla";
	public static final Integer timerFirstSpell = 45;
	public static final Integer numberOfInfoFirstSpell = 10;
	public static final Integer timeBetweenTwoBackFirstSpell = 5;
	
	public static final String nameSecondSpell = "§dYatanyaki";
	public static final Integer timerSecondSpell = 45;
	public static final Integer durationStunSecondSpell = 10;
	public static final Integer durationBetweenSecondSpell = 30;
	public static final Integer numberOfStunSecondSpell = 5;
	public static final Integer distanceSecondSpell = 15;
	
	private ArrayList<LeRosatasChaqklaInfo> listInfoChaqkla = new ArrayList<LeRosatasChaqklaInfo>();

	public LeRosatas(Player player) {
		super(player, Element.BOGDA, name);
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
		// TODO : Son
		player.sendMessage(Constants.prefixMessage + "Vous venez de mourir. Si la manche n'est pas terminée dans " + durationPassif + " secondes, vous serez ressuscité.");
		
		new BukkitRunnable() {
			private Integer timer = durationPassif;
			@Override
			public void run() {
				if (!plugin.game.isState(GameState.FIGHTING)) {
					cancel();
					return;
				}
				
				if (timer == 0) {
					player.setHealth(6);
					player.setGameMode(GameMode.ADVENTURE);
					player.teleport(plugin.game.getRound().getArena().getRandomPlayerSpawn());
					alive = true;
					
					giveEquipement();
					cancel();
					return;
				}
				
				timer--;
			}
		}.runTaskTimer(plugin, 0, 20);
		
		return false; 
	}
	
	@Override
	public void onStart() { 
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					listInfoChaqkla.removeAll(listInfoChaqkla);
					cancel();
					return;
				}

				listInfoChaqkla.add(firstSpell_CreateData());
                if (listInfoChaqkla.size() > numberOfInfoFirstSpell) {
                	listInfoChaqkla.remove(0);
                }
			}
		}.runTaskTimer(plugin, 0, 10);
		
		return; 
	}
	
	private LeRosatasChaqklaInfo firstSpell_CreateData() {
		return new LeRosatasChaqklaInfo(this);
	}
	
	@SuppressWarnings("unchecked")
	public void firstSpell_TimeRewind() {
		if (checkCooldown(nameFirstSpell, true)) {
			// TODO : Son
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f.");

			addStatus(null, Status.NOMOVE);
			
			List<LeRosatasChaqklaInfo> listInfoChaqklaCopy = (List<LeRosatasChaqklaInfo>) listInfoChaqkla.clone();
			
			new BukkitRunnable() {
				Integer index = listInfoChaqkla.size() - 1;

				@Override
				public void run() {
					if (index < 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						removeStatus(Status.NOMOVE);
						cancel();
						return;
					}

					LeRosatasChaqklaInfo info = listInfoChaqklaCopy.get(index);
					force = info.getForce();
					resis = info.getResis();
					player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(info.getMaxHealth());
					player.setHealth(info.getHealth());
					player.teleport(info.getLocation());					
					player.spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 30, 0.5, 1, 0.5, 0);
					player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1, 1);

					index--;
				}
			}.runTaskTimer(plugin, 0, timeBetweenTwoBackFirstSpell); 
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void secondSpell_TimeRoot() {
		if (checkCooldown(nameSecondSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				distanceSecondSpell,
				1.,
				(location) -> {
					location.getWorld().spawnParticle(Particle.FLASH, location, 10, 0., 0., 0., 0.);
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage +"Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f, cependant vous n'avez touché personne.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Vous avez été touché par la compétence "+ nameSecondSpell +"§f de §d"+ player.getName() +"§f.");
				
				new BukkitRunnable() {
					Integer counter = 0;

					@Override
					public void run() {
						if (counter >= numberOfStunSecondSpell || !alive || !plugin.game.isState(GameState.FIGHTING)) {
							cancel();
							return;
						}

						skylanderTarget.getPlayer().sendTitle(nameSecondSpell, "Immobilisé "+ SkylanderConverter.convertTicks(durationStunSecondSpell) +"s", 1, durationStunSecondSpell, 1);
						skylanderTarget.addStatus(10, Status.NOMOVE);
						
						counter++;
					}
				}.runTaskTimer(plugin, 0L, durationBetweenSecondSpell + durationStunSecondSpell); 
				
				player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f, vous avez touché §d"+ playerTarget.getName() +"§f.");
			}
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
		player.sendMessage("   §e▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("§f≫ §a" + namePassif + "§f, Lorsque vous mourrez, un compteur de §b" + durationPassif + " secondes§f commence. Si la manche n’est pas finie à la fin du compteur, vous êtes §ares­suscité§f.");
		player.sendMessage("\n");
		player.sendMessage("§f≫ §a" + nameFirstSpell + "§f, Vous revenez à votre état d'il y a §b" + SkylanderConverter.convertTicks(numberOfInfoFirstSpell * 10) + " secondes§f. Vous récupérez ainsi votre §aposition§f, votre §evie§f, votre §cForce§f et votre §9Résistance§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§f≫ §a" + nameSecondSpell + "§f, Vous devez viser un joueur. Si ça touche, vous l’immobilisez §b" + numberOfStunSecondSpell + " fois§f pour une durée de §b" + SkylanderConverter.convertTicks(durationStunSecondSpell) + "§f, toutes les §b" + SkylanderConverter.convertTicks(durationStunSecondSpell + durationBetweenSecondSpell) + " secondes§f. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander §cmélée§f maîtrisant des compétences temporel");
		ItemStack item = new ItemStack(Material.CLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d"+name);
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
		ItemStack item = new ItemStack(Material.CLOCK, 1);
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
		ItemStack item = new ItemStack(Material.BLUE_ICE, 1);
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
		meta.setDisplayName(nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
}
