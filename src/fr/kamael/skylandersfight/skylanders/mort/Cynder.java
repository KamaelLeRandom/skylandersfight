package fr.kamael.skylandersfight.skylanders.mort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Cynder extends Skylander {
	public static final String name = "Cynder";
	
	public static final String nameWeapon = "§7";
	public static final String namePassif = "§7Voleuse d'Âme";
	public static final Integer numberHitPassif = 15;
	public static final Integer damageBonusElementMortPassif = 2;
	public static final Double healPourcentElementViePassif = 0.5;
	
	public static final String nameFirstSpell = "§7Éclair Noire";
	public static final Integer timerFirstSpell = 20;
	public static final Double damageFirstSpell = 7.;
	public static final Integer tickSilenceFirstSpell = 200;
	public static final Integer rangeFirstSpell = 15;
	
	public static final String nameSecondSpell = "§7Vol Ténébreux";
	public static final Integer timerSecondSpell = 30;
	public static final Double rangeFlySecondSpell = 1.;
	public static final Double damageSecondSpell = 5.;

	private HashMap<Skylander, Integer> passifSkylanders = new HashMap<Skylander, Integer>();
	private ArrayList<Element> passifElements = new ArrayList<Element>();
	private Boolean havePassifVie = false;
	private Boolean havePassifFeu = false;
	private Boolean havePassifMort = false;
	
	public Cynder(Player player) {
		super(player, Element.MORT, name);
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
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (havePassifVie) {
			SpellUtils.heal(this, damage * healPourcentElementViePassif, false);
		}
		if (havePassifMort) {
			return damage + damageBonusElementMortPassif;
		}
		return damage; 
	}
	
	@Override
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (havePassifFeu) {
			skylanderHit.getPlayer().setFireTicks(100);
		}
		
		if (passifSkylanders.containsKey(skylanderHit)) {
			Integer nbHit = passifSkylanders.get(skylanderHit);
			nbHit++;
				
			if (nbHit == numberHitPassif)
				passif_Element(skylanderHit);
			
			passifSkylanders.replace(skylanderHit, nbHit);
		} else {
			passifSkylanders.put(skylanderHit, 1);
		}
		
		return false; 
	}

	public void passif_Element(Skylander skylanderHit) {
		Player playerHit = skylanderHit.getPlayer();
		Element element = skylanderHit.getElement();
		
		if (element.equals(Element.AUCUN)) return;
		
		switch (element) {
			case MAGIE: {
				if (!passifElements.contains(element)) {
					updateForce(+ Element.magieForce);
				}
				skylanderHit.updateForce(- Element.magieForce);
				break;
			}
			case TECH: {
				if (!passifElements.contains(element)) {
					updateForce(+ Element.techForce);
					updateResis(- Element.techResis);
				}
				skylanderHit.updateForce(- Element.techForce);
				skylanderHit.updateResis(- Element.techResis);
				break;
			}
			case VIE: {
				if (!passifElements.contains(element)) {
					havePassifVie = true;
				}
				break;
			}
			case MORT: {
				if (!passifElements.contains(element)) {
					havePassifMort = true;
				}
				break;
			}
			case BOGDA: {
				break;
			}
			case FEU: {
				if (!passifElements.contains(element)) {
					havePassifFeu = true;
				}
				break;
			}
			case EAU: {
				if (!passifElements.contains(element)) {
					player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Element.eauSpeed);
				}
				playerHit.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Constants.baseSpeed);
				break;
			}
			case TERRE: {
				if (!passifElements.contains(element)) {
					updateResis(- Element.terreResis);
				}
				skylanderHit.updateResis(+ Element.terreResis);
				break;
			}
			case AIR: {
				if (!passifElements.contains(element)) {
					addStatus(null, Status.NOFALL);
				}
				skylanderHit.removeStatus(Status.NOFALL);
				break;
			}
			default:
				break;
		}
	
		skylanderHit.setElement(Element.AUCUN);
		
		playerHit.playSound(playerHit.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
		playerHit.sendMessage(Constants.prefixMessage + "Vous venez de vous faire voler votre Élément '"+ element.getName() +"', vous perdez vos pouvoir liés à celui-ci.");
		playerHit.sendTitle(namePassif, "§7Vous venez de perdre votre §6Element§f.", 1, 40, 1);
		
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
		player.sendMessage(Constants.prefixMessage + "Vous venez de voler l'Élément '"+ element.getName() +"' de §c"+playerHit.getName()+"§f.");
		
		passifElements.add(element);
	}
	
	public void firstSpell_Thunder() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				rangeFirstSpell, 
				0.8, 
				(location) -> {
			        location.getWorld().spawnParticle(
				        Particle.BLOCK_CRACK, 
				        location, 
				        2, 
				        0.05, 0.05, 0.05,
				        0,
				        Material.CRYING_OBSIDIAN.createBlockData()
			        );
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé.");
				return;
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				playerTarget.playSound(playerTarget.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
				playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell +"§f de §3"+ player.getName() +".");
				playerTarget.damage(damageFirstSpell, player);
				skylanderTarget.addStatus(tickSilenceFirstSpell, Status.NOSPELL);
				
				player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell +"§f sur §3"+ playerTarget.getName() +".");
	            player.getWorld().strikeLightningEffect(playerTarget.getLocation());
				
	            Particle.DustOptions blackDust = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 1.5F);
	            for (int i = 0; i < 20; i++) {
	                double x = playerTarget.getLocation().getX() + (Math.random() - 0.5) * 2;
	                double y = playerTarget.getLocation().getY() + Math.random() * 2;
	                double z = playerTarget.getLocation().getZ() + (Math.random() - 0.5) * 2;
	                playerTarget.getWorld().spawnParticle(Particle.REDSTONE, new Location(playerTarget.getWorld(), x, y, z), 0, 0, 0, 0, 0, blackDust);
	            }
	            
				addCooldown(nameFirstSpell, timerFirstSpell);
				return;
			}
		}
	}
	
	public void secondSpell_Fly() {
		if (checkCooldown(nameSecondSpell, true)) {
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameSecondSpell +"§f.");
			
			SpellUtils.fly(
				this, 
				rangeFlySecondSpell, 
				(attacker, target) -> {
					target.getPlayer().damage(damageSecondSpell);
				}, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
					world.spawnParticle(Particle.SMOKE_NORMAL, location, 15, 0.3, 0.3, 0.3);
				}
			);
			
			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	    player.sendMessage("   ▶ " + element.getColor() + name + "§f ◀");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ namePassif +"§f, au boût de §3"+ numberHitPassif +" coups§f sur un joueur, vous lui volez son §3Élément§f ce qui vous permez de gagner ses pouvoirs (1x par élément).");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameFirstSpell +"§f, vous lancez un §3éclair noire§f sur un joueur ("+ rangeFirstSpell +" blocs maximum), celui-ci subira §3"+ damageFirstSpell +" dégats§f et sera §cSilence§f pendant "+ SkylanderConverter.convertTicks(tickSilenceFirstSpell) +" secondes. §b(" + timerFirstSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("≫ "+ nameSecondSpell +"§f, vous êtes §3envoyé dans les airs§f avec des elytras, si vous passez proche d'un joueur (- de "+ rangeFlySecondSpell +" blocs) il subira §3"+ damageSecondSpell +" dégats§f. §b(" + timerSecondSpell + "s de recharge)");
	    player.sendMessage("\n");
	    player.sendMessage("§8===============§f");
	    player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§7"+ name +"§f est un Skylander §cmélée§f capable");
		lore.add("§fde voler les Éléments des adversaires.");
		ItemStack item = new ItemStack(Material.END_CRYSTAL, 1);
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
			"§fVous lancez un §3éclair noire§f sur un joueur ("+ rangeFirstSpell +" blocs maximum), celui-ci subira §3"+ damageFirstSpell +" dégats§f et sera §cSilence§f pendant "+ SkylanderConverter.convertTicks(tickSilenceFirstSpell) +" secondes"
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
			"§fVous êtes §3envoyé dans les airs§f avec des elytras, si vous passez proche d'un joueur (- de "+ rangeFlySecondSpell +" blocs) il subira §3"+ damageSecondSpell +" dégats§f."
		);
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
