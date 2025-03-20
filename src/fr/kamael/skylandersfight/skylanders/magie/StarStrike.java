package fr.kamael.skylandersfight.skylanders.magie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
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
import fr.kamael.skylandersfight.skylanders.magie.entity.StarStrikeFireball;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class StarStrike extends Skylander {
	public static final String name = "Star Strike";
	
	public static final String nameWeapon = "§5";
	
	public static final String namePassif = "§5Étoile";
	public static final Integer delayMissPassif = 5;
	
	public static final String nameFirstSpell = "§5Chute de Météores";
	public static final Integer timerFirstSpell = 30;
	public static final Integer tickStunFirstSpell = 40;
	public static final Double rangeFireballFirstSpell = 4.5;
	public static final Double damageFireballFirstSpell = 10.;
	
	public static final String nameSecondSpell = "§5Barrière Cosmique";
	public static final Integer timerSecondSpell = 30;
	public static final Double rangeSecondSpell = 4.5;
	public static final Integer tickInvulSecondSpell = 50;
	public static final Integer tickImmoSecondSpell = 30;
	
	private Integer stackPassif = 0;
	
	public StarStrike(Player player) {
		super(player, Element.MAGIE, name);
		this.force = 1.10;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, Color.PURPLE);
		
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon(2));
		inv.setItem(2, getItemSecondSpell());
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	public Boolean onHitBow(Skylander skylanderDamager) { 
		stackPassif--;
		player.setLevel(stackPassif);
		return false;
	}
	
	public Boolean onHitSword(Skylander skylanderDamager) { 
		stackPassif--;
		player.setLevel(stackPassif);
		return false; 
	}
	
	public Boolean onDamageBow(Skylander skylanderHit, Projectile projectile) {
		if (projectile instanceof Snowball) {
			stackPassif++;
			player.setLevel(stackPassif);	
		}

		return false;
	}

	public Double addDamage(Double damage, Skylander skylanderHit) { 
		return damage + 2 + stackPassif; 
	}
	
	public void firstSpell_Meteor() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameFirstSpell + "§f.");
			
			Location location = player.getLocation().clone();
			new StarStrikeFireball(this, location.clone().add(0, 10, 0));
			new StarStrikeFireball(this, location.clone().add(2.5, 10, 2.5));
			new StarStrikeFireball(this, location.clone().add(-2.5, 10, 2.5));
			new StarStrikeFireball(this, location.clone().add(2.5, 10, -2.5));
			new StarStrikeFireball(this, location.clone().add(-2.5, 10, -2.5));
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
 	public void secondSpell_Invul() {
		if (checkCooldown(nameSecondSpell, true)) {	
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			
			SpellUtils.invulnerability(plugin, this, tickImmoSecondSpell);
			ArrayList<Skylander> listSkylanderAround = SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeSecondSpell, 2., rangeSecondSpell);

			new BukkitRunnable() {
				private Integer timer = tickInvulSecondSpell;
				@Override
				public void run() {
					if (timer == 0) {
						for (Skylander skylander : listSkylanderAround) {
							Player playerHit = skylander.getPlayer();
							skylander.addStatus(tickImmoSecondSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
							playerHit.sendTitle(nameSecondSpell, "§7Immobilisation de " + SkylanderConverter.convertTicks(tickImmoSecondSpell) + "s.", 1, tickImmoSecondSpell, 1);
							playerHit.sendMessage(Constants.prefixMessage + "Vous avez été touché par la compétence " + nameSecondSpell + "§f de §d" + player.getName() + "§f.");
							playerHit.playSound(playerHit.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
						}
					}
					
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						cancel();
						return;
					}
					
					if (timer % 5 == 0) {
						ArrayList<Skylander> listSkylanderRemove = new ArrayList<Skylander>();
						
						for (Skylander skylanderAround : listSkylanderAround) {
							Player playerAround = skylanderAround.getPlayer();
							
							if (playerAround.getLocation().distance(player.getLocation()) <= rangeSecondSpell) {
								ParticleUtils.lineParticule(Particle.FIREWORKS_SPARK, player.getLocation(), playerAround.getLocation());
							} else {
								listSkylanderRemove.add(skylanderAround);
							}
						}
						
						listSkylanderAround.removeAll(listSkylanderRemove);
					}
					
					timer--;
				}
			}.runTaskTimer(plugin, 0, 1);
			
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§5" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + namePassif + "§f, .");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + nameFirstSpell + "§f, vous invoquez des météores qui tombe du ciel, lorsque les météores touchent le sol, les §djoueurs proche§f (" + rangeFireballFirstSpell + " blocs) sont §détoudit§f pendant §d" + SkylanderConverter.convertTicks(tickStunFirstSpell) + "§f et subira §d" + damageFireballFirstSpell + " dégats§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + nameSecondSpell + "§f, vous devenez §dinvulnérable§f pendant " + SkylanderConverter.convertTicks(tickInvulSecondSpell) + " et créer des liens avec les joueurs autour de vous (" + rangeSecondSpell + " blocs), si ce §dlien n'est pas brisé§f à la fin de votre invulnérabilité, le joueur liée sera §dimmobilisé§f pendant " + SkylanderConverter.convertTicks(tickImmoSecondSpell) + " secondes. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§5"+ name +"§f est un Skylander §cdistance§f §7§n(snowball)§f§r");
		lore.add("§faugmentant ses dégats à boule de neige touché.");
		
		ItemStack item = new ItemStack(Material.SNOWBALL, 1);
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
		List<String> lore = Arrays.asList("§fVous invoquez une pluie de météore qui étourdit et inflige des dégats aux joueurs proche.");
		
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
		List<String> lore = Arrays.asList("§fVous devenez invulnérable pendant "+ SkylanderConverter.convertTicks(tickImmoSecondSpell) +"s, vous créez un lien etre vous et les joueurs proche,", "qui immobilise les joueurs liées.");
		
		ItemStack item = new ItemStack(Material.PURPLE_DYE, 1);
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

	public static ItemStack getItemWeapon(Integer nb) {
		ItemStack item = new ItemStack(Material.SNOWBALL, nb);
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
