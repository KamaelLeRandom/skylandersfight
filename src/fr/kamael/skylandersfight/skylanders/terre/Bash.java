package fr.kamael.skylandersfight.skylanders.terre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Bash extends Skylander {
	public static final String name = "Bash";
	
	public static final String nameWeapon = "§6Queue de pierre";
	public static final String namePassif = "§6Armure rocheuse";
	
	public static final String nameFirstSpell = "§6Roulade écrasante";
	public static final Integer timerFirstSpell = 20;
	public static final Double powerDashFirstSpell = 1.5;
	public static final Double rangeDashFirstSpell = 1.5;
	public static final Double damageFirstSpell = 5.;
	
	public static final String nameSecondSpell = "§6Terraformation";
	public static final Integer timerSecondSpell = 30;
	public static final Integer numberBlockMaxSecondSpell = 30;
	public static final Integer tickBlockSecondSpell = 200;

	private ArrayList<Block> listBlocks = new ArrayList<Block>();
	private Integer numberBlock = 0;
	
	public Bash(Player player) {
		super(player, Element.TERRE, name);
		resis = 0.9;
	}
	
	public void giveEquipement() {
		ItemManager.clearPlayer(player);
		ItemManager.giveColorArmor(player, element.getColorArmor());
						
		Inventory inv = player.getInventory();
		inv.setItem(0, getItemFirstSpell());
		inv.setItem(1, getItemWeapon());
		inv.setItem(2, getItemSecondSpell(numberBlockMaxSecondSpell));
		inv.setItem(9, new ItemStack(Material.ARROW));
	}
	
	@Override
	public void onStart() {
		player.setGameMode(GameMode.SURVIVAL);
		return; 
	}
	
	@Override
	public Boolean applyEnemyStrenght() { 
		return false; 
	}
	
	@Override
	public Boolean onPlace(Block block) {
		if (numberBlock >= numberBlockMaxSecondSpell) {
			return true;
		}
		
		numberBlock++;
		listBlocks.add(block);
		
		new BukkitRunnable() {
			private Integer timer = 200;
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					block.setType(Material.AIR);
					cancel();
					return;
				}
				
				timer--;
				
				if (timer == 60)
					block.setType(Material.DIRT);
				else if (timer == 20)
					block.setType(Material.COARSE_DIRT);
				
 				if (timer == 0 || block.getType().equals(Material.AIR)) {
					block.setType(Material.AIR);
					listBlocks.removeIf(block -> block.getType().equals(Material.AIR));
					numberBlock--;
					player.getInventory().addItem(getItemSecondSpell(1));
					cancel();
					return;
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		
		return false; 
	}
	
	public void firstSpell_Dash() {
		if (checkCooldown(nameFirstSpell, true)) {
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'utiliser votre compétence " + nameSecondSpell + "§f.");
			
			SpellUtils.dash(
				this, 
				player,
				powerDashFirstSpell, 
				rangeDashFirstSpell,
				15,
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.playSound(playerTarget.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameFirstSpell + "§f de §e" + player.getName() + "§f.");
					playerTarget.damage(damageFirstSpell, player);
				}, 
				(location) -> {
				    Material block = Material.DIRT;
				    location.getWorld().spawnParticle(
				        Particle.BLOCK_CRACK,
				        location,
				        20,
				        0.4, 0.1, 0.4,
				        0.1,
				        block.createBlockData()
				    );
				    
				    location.getWorld().spawnParticle(
				    	Particle.CLOUD,
				        location,
				        10,
				        0.3, 0.1, 0.3,
				        0.05
				    );
				}
			);
			
			addCooldown(nameFirstSpell, timerFirstSpell);
			return;
		}
	}
	
	public void sendDescription() {
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
		player.sendMessage("   ▶ " + element.getColor() + name + " §f◀");
		player.sendMessage("\n");
		player.sendMessage("≫ "+ namePassif +"§f, vous ignorez la §cForce§f supplémentaire de votre adversaire.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous effectuez §eune ruée§f vers l'avant qui inflige §e" + damageFirstSpell + " dégats§f aux joueur sur votre passage. §b(" + timerFirstSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous passez des §eblocs§f qui reste pendant " + SkylanderConverter.convertTicks(tickBlockSecondSpell) + " secondes. §b(" + numberBlockMaxSecondSpell + " blocs maximum à la fois)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§6"+ name +"§f est un Skylander §cmélée§f ayant de");
		lore.add("§fla capacité de pouvoir poser des blocs.");
		ItemStack item = new ItemStack(Material.COARSE_DIRT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§fVous effectuez une ruée vers l'avant qui inflige " + damageFirstSpell + " dégats aux joueurs sur votre passage."
		);
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

	public static ItemStack getItemSecondSpell(Integer number) {
		List<String> lore = Arrays.asList(
			"§fVous pouvez placer des blocs qui disparaisse au bout de "+ SkylanderConverter.convertTicks(tickBlockSecondSpell) +" secondes."
		); 
		ItemStack item = new ItemStack(Material.ROOTED_DIRT, number);
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
		meta.setDisplayName("§6" + nameWeapon);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
}
