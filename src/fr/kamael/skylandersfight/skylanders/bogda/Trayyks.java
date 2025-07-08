package fr.kamael.skylandersfight.skylanders.bogda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.bogda.entity.TrayyksFox;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Trayyks extends Skylander {
	public static final String name = "Trayyks";
	public static final String nameWeapon = "§dArme du GOAT";
	public static final String namePassif = "§dOù sont mes chaussures ?";
	
	private Skylander skylanderPassif = null;
	private ItemStack itemFirstSpell = null;
	private Integer stackMagieSpell = 1;
	private Boolean isMortSpellActive = false;
	
	public Trayyks(Player player) {
		super(player, Element.BOGDA, name);
	}

	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
						
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, plugin.random.nextBoolean() ? getItemWeaponSword() : getItemWeaponBow());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ " + namePassif + "§f, un joueur est choisi aléatoirement pour vous voler vos chaussures. Si vous parvenez à le tuer, vous gagnez l'effet §bVitesse§f de manière permanente.");
	    player.sendMessage("\n");
	    player.sendMessage("≫ " + nameFirstSpell + "§f, vous ouvrez un inventaire avec toutes les §etêtes des joueurs§f en vie. Sur celles-ci seront indiquées toutes les §6informations intéressantes§f du joueur. En cliquant, vous pouvez récupérer votre pouvoir élémentaire correspondant au joueur choisi. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ " + nameSecondSpell + "§f, vous obtenez un pouvoir élémentaire en fonction de l'élément de l'arène.");
	    player.sendMessage("\n");
	    player.sendMessage("===============");
	    player.sendMessage("\n");
	}
	
	@Override
	public void onStart() { 
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Skylander skylanderChoose = plugin.game.getPlayers().get(plugin.random.nextInt(plugin.game.getPlayers().size())).getSkylander();
				Player playerChoose = skylanderChoose.getPlayer();
				playerChoose.playSound(playerChoose.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1, 1);
				playerChoose.sendMessage(Constants.prefixMessage + "Vous venez de voler les chaussures de §d"+ playerChoose.getName() + "§f, s'il vous tue il gagnera l'effet Vitesse.");
				
				skylanderPassif = skylanderChoose;
			}
		}.runTaskLater(plugin, 100);
		
		return; 
	}
	
	@Override
	public Boolean onKill(Skylander skylanderDeath) {
		if (skylanderPassif != null && skylanderPassif.equals(skylanderDeath)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de récupérer vos chaussures, vous gagnez l'effet Vitesse de manière permanente !");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		}
		
		return false; 
	} 

	@Override
	public Double removeDamage(Double damage, Skylander skylanderHit) {
		if (isMortSpellActive)
			return 0.;
		return damage; 
	}

	public static final String nameFirstSpell = "§dTheorycraft"; 
	public static final Integer timerFirstSpell = 30;
	
	public void firstSpell_Inventory() {
		Inventory invMetamorphose = Bukkit.createInventory(player, ((plugin.game.getPlayers().size() + 8) / 9) * 9, nameFirstSpell);
		Integer idxInv = 0;
		
		for (GamePlayer gamePlayerEnemy : plugin.game.getPlayers()) {
			Player playerEnemy = gamePlayerEnemy.getPlayer();
			Skylander skylanderEnemy = gamePlayerEnemy.getSkylander();

			if (skylanderEnemy.isAlive() && !playerEnemy.equals(player)) {
	            List<String> lore = new ArrayList<>();
	            lore.add("§7Cœurs : §c" + (playerEnemy.getHealth() / 2.0) + " ❤");
	            lore.add("§7Élément : " + skylanderEnemy.getElement().getName());
	            lore.add("§7Force bonus : §6" + SkylanderConverter.convertForce(skylanderEnemy.getForce()));
	            lore.add("§7Résistance bonus : §6" + SkylanderConverter.convertResis(skylanderEnemy.getResis()));
				
				ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta itM = (SkullMeta) it.getItemMeta();
				itM.setDisplayName("§e"+playerEnemy.getName());
				itM.setOwningPlayer(playerEnemy);
	            itM.setLore(lore);
				it.setItemMeta(itM);

				invMetamorphose.setItem(idxInv, it);
				idxInv++;
			}
		}
		
		player.openInventory(invMetamorphose);
	}
	
	public void firstSpell_Apply(Player playerChoose) {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderChoose = plugin.game.getPlayer(playerChoose).getSkylander();
			
			if (itemFirstSpell != null)
				player.getInventory().remove(itemFirstSpell);
			
			giveItemFromElement(skylanderChoose.getElement());
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
		
	private static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous ouvrez un inventaire avec toutes les informations sur les joueurs."
		);
		ItemStack item = new ItemStack(Material.BOOK, 1);
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
	
	public static final String nameSecondSpell = "§dAdaptation";

	public void giveItemFromElement(Element element) {
		switch (element) {
			case MAGIE: 
				player.getInventory().addItem(getItemSecondSpellMagie());
				break;
			case TECH: 
				player.getInventory().addItem(getItemSecondSpellTech());
				break;
			case VIE: 
				player.getInventory().addItem(getItemSecondSpellVie());
				break;
			case MORT: 
				player.getInventory().addItem(getItemSecondSpellMort());
				break;
			case FEU: 
				player.getInventory().addItem(getItemSecondSpellFeu());
				break;
			case EAU: 
				player.getInventory().addItem(getItemSecondSpellEau());
				break;
			case TERRE: 
				player.getInventory().addItem(getItemSecondSpellTerre());
				break;
			case AIR: 
				player.getInventory().addItem(getItemSecondSpellAir());
				break;
			case BOGDA:
				player.getInventory().addItem(getItemSecondSpellBogda());
				break;
			default:
				break;
		}
	}

	public static final String nameSecondSpellMagie = Element.MAGIE.getColor() + "Buveuse d'Âme";
	public static final Integer timerSecondSpellMagie = 5;
	public static final Integer distanceSecondSpellMagie = 10;
	public static final Double rangeSecondSpellMagie = 1.5;

	public void secondSpell_Magie() {
		if (checkCooldown(nameSecondSpellMagie, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				distanceSecondSpellMagie, 
				rangeSecondSpellMagie, 
				(location) -> {
			        World world = location.getWorld();

			        Particle.DustOptions violetDust = new Particle.DustOptions(Color.fromRGB(153, 50, 204), 1.5f);
			        world.spawnParticle(Particle.REDSTONE, location, 5, 0.1, 0.1, 0.1, 0, violetDust);
			        world.spawnParticle(Particle.SPELL_WITCH, location, 3, 0.05, 0.05, 0.05, 0.01);
			        world.spawnParticle(Particle.SOUL, location, 2, 0.05, 0.05, 0.05, 0.01);
			        world.spawnParticle(Particle.PORTAL, location, 3, 0.1, 0.1, 0.1, 0.02);
				}
			);
			
			if (skylanderTarget == null) {
				stackMagieSpell = 1;
				player.setLevel(stackMagieSpell);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellMagie +"§f, cependant vous n'avez toucher personne donc vous perdez votre enchainement.");
			} else {
				stackMagieSpell++;
				
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.damage(stackMagieSpell);
				
				player.setLevel(stackMagieSpell);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellMagie +"§f, vous avez touché "+ playerTarget.getName() +".");
			}
			
			addCooldown(nameSecondSpellMagie, timerSecondSpellMagie);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellMagie() {
		List<String> lore = Arrays.asList(
			"§fVous lancez un rayon qui inflige 1 dégât au joueur visé. Chaque coup réussi augmente les dégâts de 1. Si vous ratez, les dégâts retombent à 1."
		);
		ItemStack item = new ItemStack(Material.PURPLE_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellMagie);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellTech = Element.TECH.getColor() + "Porte-Menton";
	public static final Integer timerSecondSpellTech = 30;
	public static final Integer durationSecondSpellTech = 10;
	public static final Double bonusSecondSpellTech = 0.20;

	public void secondSpell_Tech() {
		if (checkCooldown(nameSecondSpellTech, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellTech +"§f, vous gagnez "+ bonusSecondSpellTech*100 +"% de Force et Résistance pendant "+ durationSecondSpellTech +" secondes.");
			force += bonusSecondSpellTech;
			resis -= bonusSecondSpellTech;
			
			new BukkitRunnable() {
				private Integer timer = durationSecondSpellTech;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
						player.sendMessage(Constants.prefixMessage + "Votre compétence "+ nameSecondSpellTech +"§f vient de prendre fin.");
						force += bonusSecondSpellTech;
						resis -= bonusSecondSpellTech;
						cancel();
						return;
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 20);
			
			addCooldown(nameSecondSpellTech, timerSecondSpellTech);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellTech() {
		List<String> lore = Arrays.asList(
			"§fVous gagnez "+ bonusSecondSpellTech*100 +"% de Force et Résistance pendant "+ durationSecondSpellTech +" secondes."
		);
		ItemStack item = new ItemStack(Material.YELLOW_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellTech);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellVie = Element.VIE.getColor() + "Infect Père des Bananes";
	public static final String nameFoxSecondVie = Element.VIE.getColor() + "Bananard";
	public static final Integer timerSecondSpellVie = 30;
	public static final Integer numberOfFoxSecondSpellVie = 2;

	public void secondSpell_Vie() {
		if (checkCooldown(nameSecondSpellVie, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_FOX_EAT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellVie +"§f, vous venez d'invoquer "+ numberOfFoxSecondSpellVie +" "+ nameFoxSecondVie +"§f.");
			
	        for (int i = 0; i < numberOfFoxSecondSpellVie; i++) {
	        	new TrayyksFox(this, player.getLocation());
	        }
			
			addCooldown(nameSecondSpellVie, timerSecondSpellVie);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellVie() {
		List<String> lore = Arrays.asList(
			"§fVous invoquez "+ numberOfFoxSecondSpellVie + " "+ nameFoxSecondVie +"§f."
		);
		ItemStack item = new ItemStack(Material.LIME_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellVie);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellMort = Element.MORT.getColor() + "Coeur de Lion";
	public static final Integer timerSecondSpellMort = 45;
	public static final Integer durationSecondSpellMort = 10;
	
	public void secondSpell_Mort() {
		if (checkCooldown(nameSecondSpellMort, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellMort +"§f, vous ne subissez plus aucun dégat pendant "+ durationSecondSpellMort +" secondes.");
			
			isMortSpellActive = true;
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
					player.sendMessage(Constants.prefixMessage + "Votre compétence "+ nameSecondSpellMort +"§f vient de prendre fin.");
					isMortSpellActive = false;
					cancel();
					return;
				}
			}.runTaskLater(plugin, durationSecondSpellMort * 20);
			
			addCooldown(nameSecondSpellMort, timerSecondSpellMort);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellMort() {
		List<String> lore = Arrays.asList(
			"§fVous ne subissez plus aucun dégats venant d'un joueur pendant "+ durationSecondSpellMort +" secondes."
		);
		ItemStack item = new ItemStack(Material.BLACK_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellMort);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellFeu = Element.FEU.getColor() + "#Grilled";
	public static final Integer timerSecondSpellFeu = 20;
	public static final Integer distanceSecondSpellFeu = 15;
	public static final Integer durationFireSecondSpellFeu = 10;
	public static final Double damageSecondSpellFeu = 6.;
	
	public void secondSpell_Feu() {
		if (checkCooldown(nameSecondSpellFeu, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				distanceSecondSpellFeu, 
				1.5, 
				(location) -> {
			        Location origin = player.getEyeLocation();
			        Vector direction = origin.getDirection().normalize();

			        for (double i = 0; i < 6; i += 0.3) {
			            Location point = origin.clone().add(direction.clone().multiply(i));
			            player.getWorld().spawnParticle(Particle.FLAME, point, 2, 0.05, 0.05, 0.05, 0.01);
			            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, point, 1, 0.05, 0.05, 0.05, 0.01);
			            player.getWorld().spawnParticle(Particle.LAVA, point, 1, 0, 0, 0, 0);
			        }
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellFeu +"§f, cependant vous n'avez toucher personne.");
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Vous avez été toucher par la compétence "+ nameSecondSpellFeu +"§f de "+ player.getName() +".");
				playerTarget.setFireTicks(durationFireSecondSpellFeu * 20);
				playerTarget.damage(damageSecondSpellFeu, player);
				
				player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellFeu +"§f, vous avez transformer "+ playerTarget.getName() +" en charbon.");
			}
			
			addCooldown(nameSecondSpellFeu, timerSecondSpellFeu);
			return;
		}
	}

	private static ItemStack getItemSecondSpellFeu() {
		List<String> lore = Arrays.asList(
			"§fVous ciblez un joueur (- de "+ distanceSecondSpellFeu +" blocs), celui-ci sera brulé pendant "+ durationFireSecondSpellFeu +" secondes et subira "+ damageSecondSpellFeu +" dégats."
		);
		ItemStack item = new ItemStack(Material.RED_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellFeu);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellEau = Element.EAU.getColor() + "Direction Malte";
	public static final Integer timerSecondSpellEau = 10;
	public static final Integer durationSlowSecondSpellEau = 200;
	public static final Double damageSecondSpellEau = 3.;
	public static final Double valueSecondSpellEau = 1.3;
	
	public void secondSpell_Eau() {
		if (checkCooldown(nameSecondSpellEau, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellEau +"§f, vous venez de faire une ruée l'avant.");
			
			SpellUtils.dash(
				this, 
				player, 
				valueSecondSpellEau,
				1.5, 
				15, 
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.damage(damageSecondSpellEau);
					playerTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationSlowSecondSpellEau, 1, false, false));
				}, 
			    (location) -> {
			        World world = location.getWorld();
			        world.spawnParticle(Particle.WATER_SPLASH, location, 10, 0.2, 0.1, 0.2, 0.05);
			        world.spawnParticle(Particle.DRIP_WATER, location, 5, 0.1, 0.2, 0.1, 0.01);
			        world.spawnParticle(Particle.BUBBLE_POP, location, 3, 0.1, 0.1, 0.1, 0.02);
			        world.spawnParticle(Particle.CLOUD, location.clone().add(0, 0.1, 0), 2, 0.2, 0, 0.2, 0.01);
			    }
			);
			
			addCooldown(nameSecondSpellEau, timerSecondSpellEau);
			return;
		}
	}

	private static ItemStack getItemSecondSpellEau() {
		List<String> lore = Arrays.asList(
			"§fVous effectuez une ruée vers l'avant qui inflige "+ damageSecondSpellEau +" dégats et l'effet Ralentissement pendant "+ SkylanderConverter.convertTicks(durationSlowSecondSpellEau) +" aux joueurs sur votre passage."
		);
		ItemStack item = new ItemStack(Material.BLUE_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellEau);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellTerre = Element.TERRE.getColor() + "On devient pas Gold 3 par hasard";
	public static final Integer timerSecondSpellTerre = 30;
	public static final Integer durationStunSecondSpellTerre = 50;
	public static final Double rangeSecondSpellTerre = 5.;
	
	public void secondSpell_Terre() {
		if (checkCooldown(nameSecondSpellTerre, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_ROOTED_DIRT_BREAK, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellTerre +"§f, vous avez étoudit tout les joueurs autour pendant "+ SkylanderConverter.convertTicks(durationStunSecondSpellTerre) +" secondes.");
			
			ParticleUtils.sphereParticule(plugin, player.getLocation(), Particle.SMOKE_NORMAL, rangeSecondSpellTerre);
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpellTerre, 2., rangeSecondSpellTerre)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_NETHER_GOLD_ORE_BREAK, 1, 1);
				playerHit.sendTitle(nameFirstSpell, "§7Étourdit pendant "+ SkylanderConverter.convertTicks(durationStunSecondSpellTerre) +"s", 2, durationStunSecondSpellTerre, 2);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être toucher par la compétence "+ nameSecondSpellTerre +"§f de §d"+ player.getName() +"§f.");
				skylanderHit.addStatus(durationStunSecondSpellTerre, Status.NOMOVE, Status.NOMAKEDAMAGE, Status.NOSPELL);
			}
			
			addCooldown(nameSecondSpellTerre, timerSecondSpellTerre);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellTerre() {
		List<String> lore = Arrays.asList(
			"§fVous étoudissez tout les joueurs autour de vous (- de "+ rangeSecondSpellTerre +" blocs) pendant "+ SkylanderConverter.convertTicks(durationStunSecondSpellTerre) +" secondes."
		);
		ItemStack item = new ItemStack(Material.BROWN_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellTerre);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellAir = Element.AIR.getColor() + "Papillonnage";
	public static final Integer timerSecondSpellAir = 30;
	public static final Integer durationSecondSpellAir = 200;
	public static final Integer numberOfButterflySecondSpellAir = 25;
	
	public void secondSpell_Air() {
		if (checkCooldown(nameSecondSpellAir, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_BAT_LOOP, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpellAir +"§f, vous devenez invisible pendant "+ SkylanderConverter.convertTicks(timerSecondSpellAir) +" secondes.");
			
			SpellUtils.invisibility(plugin, this, durationSecondSpellAir);
			
	        List<Bat> butterflys = new ArrayList<>();
	        for (int i = 0; i < numberOfButterflySecondSpellAir; i++) {
	            Location spawnLoc = player.getLocation().clone().add(Math.random() * 4 - 2, Math.random() * 4 - 2, Math.random() * 4 - 2);
	            Bat butterfly = (Bat) player.getWorld().spawnEntity(spawnLoc, EntityType.BAT);
	            butterfly.setSilent(true); 
	            butterfly.setInvulnerable(true);
	            butterfly.setCollidable(false);
	            butterflys.add(butterfly);
	        }

	        new BukkitRunnable() {
	            @Override
	            public void run() {
	                for (Bat bat : butterflys) {
	                    if (!bat.isDead()) {
	                        bat.remove();
	                    }
	                }
	                cancel();
	                return;
	            }
	        }.runTaskLater(plugin, durationSecondSpellAir);
			
			addCooldown(nameSecondSpellAir, timerSecondSpellAir);
			return;
		}
	}

	private static ItemStack getItemSecondSpellAir() {
		List<String> lore = Arrays.asList(
			"§fVous devenez invisible pendant "+ SkylanderConverter.convertTicks(durationSecondSpellAir) +" secondes, vous laissez derrière vous plein de papillons."
		);
		ItemStack item = new ItemStack(Material.WHITE_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellAir);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static final String nameSecondSpellBogda = Element.BOGDA.getColor() + "Tribunal des Bogda";
	public static final Integer timerSecondSpellBogda = 30;
	public static final Location locationPlayerSecondSpellBogda = new Location(Bukkit.getWorld("world"), -1111.5, 4.5, 156.5);
	public static final Location locationEnemySecondSpellBogda = new Location(Bukkit.getWorld("world"), -1111.5, 4.5, 130.5);
	
	public void secondSpell_Bogda() {
		if (checkCooldown(nameSecondSpellBogda, true)) {
			
			addCooldown(nameSecondSpellBogda, timerSecondSpellBogda);
			return;
		}
	}
	
	private static ItemStack getItemSecondSpellBogda() {
		List<String> lore = Arrays.asList(
			"§f."
		);
		ItemStack item = new ItemStack(Material.PINK_DYE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(nameSecondSpellBogda);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getItemWeaponSword() {
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
	
	private static ItemStack getItemWeaponBow() {
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
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander totalement §caléatoire§f, il possède une adaptabilité hors norme ce qui le rend très imprévisible.");
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
