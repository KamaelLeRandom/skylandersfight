package fr.kamael.skylandersfight.skylanders.bogda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
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
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class ZemZem extends Skylander {
	public static final String name = "Zem'Zem";
	
	public static final String nameWeapon = "§dSniper";
	public static final String namePassif = "§dPour la Bogda !";
	public static final Double bonusResisPassif = 0.05;
	public static final Double bonusForcePassif = 0.02;
	
	public static final String nameFirstSpell = "§dAvis de recherche";
	public static final Integer timerFirstSpell = 45;
	public static final Integer durationFirstSpell = 45;
	public static final Integer durationSlowFirstSpell = 15;
	public static final Double lifeCostFirstSpell = 4.;
	public static final List<String> reasonFirstSpell = Arrays.asList(
		"vente d'extracteur de jus illégale", 
		"arnaque à la formation", 
		"réinvite la définition des mots",
		"a le chèque de sa société sur le bureau",
		"fausse carte Google Play de 10€");
	
	public static final String nameSecondSpell = "§dExpulsion du territoire";
	public static final Integer timerSecondSpell = 5;
	public static final Double lifeCostSecondSpell = 4.;
	public static final Double rangeSecondSpell = 4.;
	
	private Boolean passif = false;

	public ZemZem(Player player) {
		super(player, Element.BOGDA, name);
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
						
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW, 64));
		inv.setItem(10, new ItemStack(Material.ARROW, 64));
	}
	
	@Override
	public void onStart() { 
		ArrayList<GamePlayer> listAllGamePlayer = plugin.game.getPlayers();
		Boolean allBogda = true;
		Boolean noneBogda = true;
		
		for (GamePlayer gamePlayer : listAllGamePlayer) {
			if (gamePlayer.getSkylander().getElement().equals(Element.BOGDA)) {
				resis -= bonusResisPassif;
				noneBogda = false;
			} else {
				force += bonusForcePassif;
				allBogda = false;
			}
		}
		
		if (allBogda == false && noneBogda == false) {
			new BukkitRunnable() {
				private Boolean allBogda = true;
				private Boolean noneBogda = true;
				
				@Override
				public void run() {
					if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					allBogda = true;
					noneBogda = true;
					
					for (GamePlayer gamePlayer : plugin.game.getPlayers())
						if (gamePlayer.getSkylander().isAlive())
							if (gamePlayer.getSkylander().getElement().equals(Element.BOGDA))
								noneBogda = false;
							else
								allBogda = false;
					
					if (allBogda) {
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Vous avez §areussi§f votre mission.");
						player.sendTitle(namePassif, "§aReussite§7 de la mission !!", 5, 40, 5);
						passif = true;
						cancel();
						return;
					}
					
					if (noneBogda) {
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Vous avez §céchouer§f votre mission.");
						player.sendTitle(namePassif, "§cÉchec§7 de la mission...", 5, 40, 5);
						passif = false;
						cancel();
						return;
					}
				}
			}.runTaskTimer(plugin, 0, 20);
		}
	}
	
	@Override
	public Double addDamage(Double damage, Skylander skylanderHit) { 
		if (passif)
			return damage*2;
		return damage; 
	}
	
	public void firstSpell_Inventory() {
		if (checkCooldown(nameFirstSpell, true)) {
			if (player.getHealth() > lifeCostFirstSpell) {
				
				Inventory invPassif = Bukkit.createInventory(player, ((plugin.game.getPlayers().size() + 8) / 9) * 9, nameFirstSpell);
				Integer invIndex = 0;
				
				for (GamePlayer gamePlayerEnemy : plugin.game.getPlayers()) 
				{
					Skylander skylanderEnemy = gamePlayerEnemy.getSkylander();
					Player playerEnemy = gamePlayerEnemy.getPlayer();

					if (skylanderEnemy.isAlive() && !skylanderEnemy.getElement().equals(Element.BOGDA) && !playerEnemy.equals(player)) {
						ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
						SkullMeta itM = (SkullMeta) it.getItemMeta();
						itM.setDisplayName("§d"+playerEnemy.getName());
						itM.setOwningPlayer(playerEnemy);
						it.setItemMeta(itM);
						
						invPassif.setItem(invIndex, it);
						invIndex++;
					}
				}
				player.openInventory(invPassif);
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez de vie pour lancer votre compétence "+ nameFirstSpell +"§f.");
				return;
			}	
		}
	}
	
	public void firstSpell_Apply(Player playerChoose) {
		String msg = Constants.prefixMessage + "§c§l/!\\ AVIS DE RECHERCHE /!\\ §r§f le joueur §c"+ playerChoose.getName() +" est activement recherché pour la raison : §d"+ reasonFirstSpell.get(plugin.random.nextInt(reasonFirstSpell.size())) + "§f !";
		for (int i = 0; i <= 3; i++)
			Bukkit.broadcastMessage(msg);
		
		Skylander skylanderChoose = plugin.game.getPlayer(playerChoose).getSkylander();
		skylanderChoose.addStatus(durationFirstSpell, Status.RECHERCHE);
		playerChoose.playSound(playerChoose.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
		playerChoose.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell +"§f de "+ player.getName() + "§f.");
		playerChoose.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationFirstSpell * 20, 0, false, false));
		playerChoose.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationSlowFirstSpell * 20, 0, false, false));
		
		player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur "+ playerChoose.getName() + "§f.");
		player.damage(lifeCostFirstSpell);
		addCooldown(nameFirstSpell, timerFirstSpell);
		return;
	}
	
	public void secondSpell_Teleport() {
		if (checkCooldown(nameSecondSpell, true)) {
			if (player.getHealth() > lifeCostSecondSpell) {
				ArrayList<Skylander> skylandersAround = SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell);
				
				if (skylandersAround.isEmpty()) {
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Aucun joueur n'est proche vous, votre compétence "+ nameSecondSpell +"§f ne peut pas s'activer.");
					return;
				} else {
					ParticleUtils.sphereParticule(plugin, player.getLocation(), Particle.SMOKE_LARGE, rangeSecondSpell);
					for (Skylander skylanderAround : skylandersAround) {
						Player playerAround = skylanderAround.getPlayer();
						playerAround.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
						playerAround.sendMessage(Constants.prefixMessage + "Vous venez d'être retéléporter aléatoirement car vous avez été touché par la compétence "+ nameSecondSpell +" de "+ player.getName() +"§f.");
						playerAround.teleport(plugin.game.getRound().getArena().getRandomPlayerSpawn());
					}
					
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Aucun joueur n'est proche vous, votre compétence "+ nameSecondSpell +"§f ne peut pas s'activer.");
					player.damage(lifeCostSecondSpell);
					addCooldown(nameSecondSpell, timerSecondSpell);
					return;
				}
			} else {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez pas assez de vie pour lancer votre compétence "+ nameSecondSpell +"§f.");
				return;
			}
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
		player.sendMessage("   §f▶ " + element.getColor() + name + "§f ◀");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + namePassif + "§f, pour chaque Skylander présent dans la manche, vous gagnez un bonus selon son élément. S'il s'agit d'un "+ Element.BOGDA.getName() +", vous gagnez §6" + bonusResisPassif * 100 + "%§f de Résistance ; sinon, vous gagnez §6" + bonusForcePassif * 100 + "%§f de Force. De plus, vous possédez une mission supplémentaire : s’il ne reste plus que des Skylanders d’élément "+ Element.BOGDA.getName() +" en vie, vous doublez tous vos dégâts infligés. §7(Ce bonus ne s’active que s’il n’y a pas uniquement des Bogda au début de la partie)");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameFirstSpell + "§f, vous lancez un avis de recherche sur un joueur pour une durée de §b" + durationFirstSpell + " secondes§f. Celui-ci sera mis en surbrillance, et le joueur qui le tue sera entièrement soigné. Cependant, vous perdez §c" + lifeCostFirstSpell / 2 + "§f cœurs. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§f≫ " + nameSecondSpell + "§f, vous §etéléportez aléatoirement§f tous les joueurs autour de vous (dans un rayon de §e" + rangeSecondSpell + " blocs§f). Cependant, vous perdez §c" + lifeCostSecondSpell / 2 + "§f cœurs. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("§6§l===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander §cdistance§f ayant pour objectif d'éliminer les joueurs différent de lui pour devenir surpuissant");
		ItemStack item = new ItemStack(Material.IRON_BARS, 1);
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
		ItemStack item = new ItemStack(Material.PAINTING, 1);
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
		ItemStack item = new ItemStack(Material.IRON_BARS, 1);
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
		ItemStack item = new ItemStack(Material.CROSSBOW, 1);
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
