package fr.kamael.skylandersfight.skylanders.bogda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Cyroule extends Skylander {
	public static final String name = "Cyroule Nouhanah";
	
	public static final String nameWeapon = "§dPoignard";
	public static final String namePassif = "§dContrat";
	public static final Integer numberOfHitPassif = 25;
	
	public static final String nameFirstSpell = "§dCoup de Pression";
	public static final Integer timerFirstSpell = 30;
	public static final Integer distanceFirstSpell = 10;
	public static final Double rangeFirstSpell = 1.;
	public static final Integer secDurationFirstSpell = 5;
	
	public static final String nameSecondSpell = "§dPrime Time";
	public static final Integer timerSecondSpell = 60;
	public static final Location locationSecondSpell = new Location(Bukkit.getWorld("world"), -1112, 6, 180);
	public static final Double rangeSecondSpell = 15.;
	public static final Double bonusSecondSpell = 0.5;
	public static final Integer secDurationSecondSpell = 15;

	private HashMap<Skylander, Integer> skylandersHitPassif = new HashMap<Skylander, Integer>();
	private Skylander skylanderPassif = null;
	private Skylander skylanderFirstSpell = null;
	
	public Cyroule(Player player) {
		super(player, Element.BOGDA, name);
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
	public Boolean onHitBow(Skylander skylanderDamager) {
		if (skylanderDamager.equals(skylanderFirstSpell))
			return true;
		return false; 
	} 
	
	@Override
	public Boolean onHitSword(Skylander skylanderDamager) { 
		if (skylanderDamager.equals(skylanderFirstSpell))
			return true;
		return false; 
	}
	
	@Override
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (skylandersHitPassif.containsKey(skylanderHit)) {
			Integer nbHit = skylandersHitPassif.get(skylanderHit);
			if (nbHit < numberOfHitPassif)
				skylandersHitPassif.replace(skylanderHit, nbHit+1);
		} else {
			skylandersHitPassif.put(skylanderHit, 1);
		}
		
		return false; 
	}
	
	@SuppressWarnings("deprecation")
	public void passif_Inventory() {
		ArrayList<GamePlayer> gamePlayers = plugin.game.getPlayers();
		gamePlayers.removeIf(p -> !p.getSkylander().isAlive());
		
		if (gamePlayers.size() <= 2) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous ne pouvez pas effectuer de "+ namePassif +"§f lorsqu'il n'y a plus que deux joueurs ou moins en vie.");
			return;
		}
		
		Inventory invPassif = Bukkit.createInventory(player, ((gamePlayers.size() + 8) / 9) * 9, namePassif);
		Integer idxInv = 0;
		
		for (Skylander skylanderEnemy : skylandersHitPassif.keySet()) {
			if (skylanderEnemy.isAlive()) {
				Player playerEnemy = skylanderEnemy.getPlayer();
				Integer nbHit = skylandersHitPassif.get(skylanderEnemy);
				
				ItemStack it = new ItemStack(Material.PLAYER_HEAD, nbHit);
				SkullMeta itM = (SkullMeta) it.getItemMeta();
				itM.setDisplayName("§dCorruption §c"+playerEnemy.getName());
				itM.setLore(Arrays.asList("§fVous avez frappé §c"+ nbHit +" §f fois §c"+playerEnemy.getName()+"§f."));
				itM.setOwner(playerEnemy.getName());
				it.setItemMeta(itM);
				
				invPassif.setItem(idxInv, it);
			}
		}
		
		if (skylanderPassif != null && skylanderPassif.isAlive()) {
			ItemStack it = new ItemStack(Material.BARRIER);
			ItemMeta itM = it.getItemMeta();
			itM.setDisplayName("§cAnnuler le contrat actuel");
			it.setItemMeta(itM);
			
			invPassif.setItem(invPassif.getSize() - 1, it);
		}
		
		player.openInventory(invPassif);
	}
	
	public void passif_Cancel() {
		if (skylanderPassif != null && skylanderPassif.isAlive()) {
			Player playerPassif = skylanderPassif.getPlayer();
			playerPassif.playSound(playerPassif.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
			playerPassif.sendTitle(namePassif, "§cAnnulation§f, vous êtes tout seul maintenant", 1, 25, 1);
			playerPassif.sendMessage(Constants.prefixMessage + "Le "+ namePassif +"§f qui vous a été annulé, vous devez re-gagner seul.");
			
			GamePlayer gamePlayerPassif = plugin.game.getPlayer(playerPassif);
			gamePlayerPassif.setActualTeam(gamePlayerPassif.getInitialTeam());
			
			skylandersHitPassif.remove(skylanderPassif);
			skylanderPassif = null;
			
			player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'§cannuler§f votre "+ namePassif +"§f sur le joueur §5"+ playerPassif.getName() +"§f.");
		}
	}
	
	public void passif_Apply(Player playerChoose) {
		if (skylanderPassif != null) {
			if (skylanderPassif.isAlive()) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous avez déjà mis un "+ namePassif +"§f sur §5"+ skylanderPassif.getPlayer().getName() +"§f, si vous voulez changer vous devez annuler votre "+ namePassif +"§f actuel.");
				return;
			} else {
				skylanderPassif = null;
			}			
		} 
		
		GamePlayer gamePlayerChoose = plugin.game.getPlayer(playerChoose);
		Skylander skylanderChoose = gamePlayerChoose.getSkylander();
		
		if (skylandersHitPassif.get(skylanderChoose) >= numberOfHitPassif) {
			player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'imposer votre "+ namePassif +"§f au joueur §5"+ playerChoose.getName() +"§f.");
			
			playerChoose.playSound(playerChoose.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
			playerChoose.sendTitle(namePassif, "§7Vous devez gagner pour §d"+ player.getName() +"§f.", 1, 25, 1);
			playerChoose.sendMessage(Constants.prefixMessage + "§d" + player.getName() +"§f vient de vous imposez un "+ namePassif +"§f, vous devez gagner pour lui tant que le "+ namePassif +"§f est actif.");
		
			skylanderPassif = skylanderChoose;
			
			gamePlayerChoose.setActualTeam(plugin.game.getPlayer(player).getActualTeam());
		} else {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez frapper §c"+ playerChoose.getName() +"§f pour lui imposer un "+ nameWeapon +"§f.");
			return;
		}
	}
	
	public void firstSpell_NoAttack() {
		if (checkCooldown(nameFirstSpell, true)) {
			skylanderFirstSpell = SpellUtils.targetPlayer(
				this, 
				distanceFirstSpell, 
				rangeFirstSpell, 
				(location) -> {
					location.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, location, 10, 0., 0., 0., 0.);
				}
			);
			
			if (skylanderFirstSpell == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
				return;
			} else {
				Player playerHit = skylanderFirstSpell.getPlayer();
				playerHit.playSound(player.getLocation(), Sound.ENTITY_ZOGLIN_ANGRY, 1, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell + "§f de §5"+ player.getName() +"§f, vous ne pouvez plus l'attaquer pendant "+ secDurationFirstSpell + " secondes.");
				playerHit.sendTitle(nameFirstSpell, "Vous ne pouvez attaquer" + player.getName(), 1, 25, 1);
				
				new BukkitRunnable() {
					private Integer timer = secDurationFirstSpell;
					@Override
					public void run() {
						if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
							skylanderFirstSpell = null;
							playerHit.sendMessage(Constants.prefixMessage + "La compétence "+ nameFirstSpell + "§f de §5"+ player.getName() +"§f a pris fin.");
							cancel();
							return;
						}
						
						timer--;
					}
				}.runTaskTimer(plugin, 0, 20);
				
				player.playSound(player.getLocation(), Sound.ENTITY_ZOGLIN_ANGRY, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §5"+ playerHit.getName() +"§f.");
				
				addCooldown(nameFirstSpell, timerFirstSpell);
			}
		}
	}
	
	public void secondSpell_Arena() {
		if (checkCooldown(nameSecondSpell, true)) {
			HashMap<Skylander, Location> skylandersOldLocation = new HashMap<Skylander, Location>();
			ArrayList<Skylander> skylandersHit = SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 5., rangeSecondSpell);
			
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de lancer votre "+ nameSecondSpell +"§f sur §c"+ skylandersHit.size() +"§f joueurs.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0, false, false));
			force += bonusSecondSpell;
			resis -= bonusSecondSpell;
			
			skylandersHit.add(this);
			if (skylanderPassif != null && skylanderPassif.isAlive() && !skylandersHit.contains(skylanderPassif))
				skylandersHit.add(this.skylanderPassif);
		
			for (Skylander skylanderHit : skylandersHit) {
				Player playerPT = skylanderHit.getPlayer();
				skylandersOldLocation.put(skylanderHit, playerPT.getLocation());
				playerPT.sendTitle("§5Extension du Territoire", nameSecondSpell, 1, 20, 1);
				playerPT.teleport(locationSecondSpell);
				skylanderHit.addStatus(null, Status.NOFLY, Status.NOTELEPORT);
				skylanderHit.addStatus(30, Status.NOTAKEDAMAGE);
			}
			
			new BukkitRunnable() {
				private Integer timer = secDurationSecondSpell;
				@Override
				public void run() {
					if (timer == 0 || !plugin.game.isState(GameState.FIGHTING)) {
						force -= bonusSecondSpell;
						resis += bonusSecondSpell;
						
						for (Skylander skylanderHit : skylandersHit)
							if (skylanderHit.isAlive())
								skylanderHit.getPlayer().teleport(skylandersOldLocation.get(skylanderHit));
						
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
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
		player.sendMessage("≫ "+ namePassif +"§f, vous pouvez recruter un joueur dans la partie afin qu'il gagne avec vous, cependant pour ce faire vous devez lui infliger au minimum "+ numberOfHitPassif +" coups avant. Pour changer de joueur, vous devez annuler l'ancien.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous pouvez empecher le joueur ciblé (- de "+ distanceFirstSpell +" blocs) de vous attaquez pendant "+ secDurationFirstSpell +" secondes. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous teleportez tout les joueurs autour de vous (- de "+ rangeSecondSpell +" blocs) ainsi que vous même dans votre §dPlateau TPMP§f pour une durée de "+ secDurationSecondSpell +" secondes. A l'intérieur vous gagnez §6"+ bonusSecondSpell*100 +"%§f de §cForce§f et §cRésistance§f supplémentaire. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander §cmélée§f capable");
		lore.add("§fde recruter un joueur pour qu'il gagne avec vous.");
		ItemStack item = new ItemStack(Material.PAINTING, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemPassif() {
		List<String> lore = Arrays.asList(
			"§fVous pouvez recruter un joueur pour qu'il gagne avec vous, cependant il vous faut le frapper au moins "+ numberOfHitPassif +" minimum."
		);
		ItemStack item = new ItemStack(Material.CHEST, 1);
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
		List<String> lore = Arrays.asList(
			"§fVous empechez le joueur ciblé (- de "+ distanceFirstSpell +" blocs) de vous attaquez pendant "+ secDurationFirstSpell +" secondes."
		);
		ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
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
			"§fVous téléportez tout les joueurs autour de vous (- de "+ rangeSecondSpell +" blocs) à l'intérieur de votre §dPlateau TPMP§f,", 
			"§fpour une durée de "+ secDurationSecondSpell +" secondes."
		);
		ItemStack item = new ItemStack(Material.PAINTING, 1);
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
