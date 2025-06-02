package fr.kamael.skylandersfight.skylanders.magie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.ParticleUtils;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Voodood extends Skylander {
	public static final String name = "Voodood";
	
	public static final String nameWeapon = "§5Hache de Foudre";
	
	public static final String namePassif = "§5Chargement";
	public static final Integer stackMaxPassif = 5;
	
	public static final String nameFirstSpell = "§5Électrocution";
	public static final Integer timerFirstSpell = 30;
	public static final Integer rangePlayerFirstSpell = 12;
	public static final Double rangeDetectFirstSpell = 0.75;
	public static final Integer damageFirstSpell = 5;
	public static final Integer tickStunFirstSpell = 40;
	
	public static final String nameSecondSpell = "§5Téléportation";
	public static final Integer timerSecondSpell = 30;
	public static final Integer rangeNeededSecondSpell = 15;
	public static final Double penaltyResisSecondSpell = 0.1;
	
	private Skylander lastSkylanderHit = null;
	private Integer stackPassif = 0;
	
	public Voodood(Player player) {
		super(player, Element.MAGIE, name);
		this.force = 1.10;
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
	
	public Boolean onDamageSword(Skylander skylanderHit) { 
		if (stackPassif < stackMaxPassif) {
			stackPassif++;
			player.setLevel(stackPassif);
		}
		
		lastSkylanderHit = skylanderHit;
		
		return false; 
	}
	
	public void firstSpell_Stun() {
		if (checkCooldown(nameFirstSpell, true)) {
			Skylander skylanderTarget = SpellUtils.targetPlayer(
				this, 
				rangePlayerFirstSpell, 
				rangeDetectFirstSpell, 
				(location) -> {
					location.getWorld().spawnParticle(
				        Particle.BLOCK_CRACK, 
				        location, 
				        2, 
				        0.05, 0.05, 0.05,
				        0,
				        Material.END_ROD.createBlockData()
					);
				}
			);
			
			if (skylanderTarget == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Aucun joueur n'a été trouvé.");
				return;
			} else {
				Player playerTarget = skylanderTarget.getPlayer();
				if (stackPassif == stackMaxPassif) {
					playerTarget.damage(damageFirstSpell * 2, player);
					stackPassif = 0;
					player.setLevel(stackPassif);
				}
				else
					playerTarget.damage(damageFirstSpell, player);
				playerTarget.getWorld().strikeLightningEffect(playerTarget.getLocation());
				playerTarget.sendMessage("Vous avez été touché par la compétence " + nameFirstSpell + "§f de §d" + player.getName() + "§f.");				
				skylanderTarget.addStatus(tickStunFirstSpell, Status.NOMOVE, Status.NOSPELL, Status.NOMAKEDAMAGE);
				
				player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence "+ nameFirstSpell + "§f sur §d" + playerTarget.getName() + "§f.");
				
				addCooldown(nameFirstSpell, timerFirstSpell);
				return;
			}
		}
	}
	
	public void secondSpell_Teleportation() {
		if (checkCooldown(nameSecondSpell, true)) {
			if (lastSkylanderHit == null) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Vous n'avez encore frappé personne.");
				return;
			}
			
			if (!lastSkylanderHit.isAlive()) {
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
				player.sendMessage(Constants.prefixMessage + "Le joueur " + lastSkylanderHit.getPlayer().getName() + " a été éliminé.");
				lastSkylanderHit = null;
				return;
			}
			
			Player playerTarget = lastSkylanderHit.getPlayer();
			playerTarget.playSound(playerTarget.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, 1);
			
			if (stackPassif == stackMaxPassif) {
				lastSkylanderHit.updateResis(penaltyResisSecondSpell);
				stackPassif = 0;
				player.setLevel(stackPassif);
			}
	
			ParticleUtils.teleportationParticule(player.getLocation());
			player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de vous téléportez à §d" + playerTarget.getName() + "§f grâce à votre compétence " + nameSecondSpell + "§f.");
			player.teleport(playerTarget.getLocation().clone().add(playerTarget.getLocation().getDirection().multiply(-1)));
			ParticleUtils.teleportationParticule(player.getLocation());

			addCooldown(nameSecondSpell, timerSecondSpell);
			return;
		}
	}

	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶§5" + name + "§f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + namePassif + "§f, à chaque coup que vous §dinfligez des dégats§f vous gagnez un compteur " + namePassif + "§f, au bout de §d" + stackMaxPassif + " compteurs§f votre prochaine §dcompétence§f est §damélioré§f.");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + nameFirstSpell + "§f, vous §dciblez un joueur§f, celui-ci sera §détoudit§f pendant §d" + SkylanderConverter.convertTicks(tickStunFirstSpell) + " secondes§f et subira §d" + damageFirstSpell + " dégats§f. Si votre " + namePassif + "§f est complet, les §ddégats§f infligés sont §ddoublés§f. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ §5" + nameSecondSpell + "§f, vous êtes §dtéléporté§f au §ddernier joueur§f que vous avez frappé si celui-ci est à §dmoins de " + rangeNeededSecondSpell + " blocs§f. Si votre " + namePassif + "§f est complet, le joueur perd §d" + penaltyResisSecondSpell*100 + " de Résistance§f. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}

	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§5"+ name +"§f est un Skylander §cmélée§f pouvant suivre");
		lore.add("§fsa cible jusqu'à son élimination.");
		
		ItemStack item = new ItemStack(Material.GOLDEN_AXE, 1);
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
		List<String> lore = Arrays.asList("§fVous ciblez un joueur qui sera §détoudit pendant " + SkylanderConverter.convertTicks(tickStunFirstSpell) + "s§f", "§fsubira §d" + damageFirstSpell + " dégats§f.");
		
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
		List<String> lore = Arrays.asList("§fVous êtes §dtéléporté au dernier joueur§f que vous avez frappé.");
		
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
