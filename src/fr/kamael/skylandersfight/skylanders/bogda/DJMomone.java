package fr.kamael.skylandersfight.skylanders.bogda;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.ArmorStand;
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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.game.GamePlayer;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Element;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.utils.SpellUtils;
import fr.kamael.skylandersfight.utils.converter.SkylanderConverter;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class DJMomone extends Skylander {
	public static final String name = "DJ-Momone";
	
	public static final String nameWeapon = "§dMicrophone";
	public static final String namePassif = "§dL'Ange Momone";
	public static final Double probaPassif = 0.1;
	public static final Double bonusPassif = 2.;
	
	public static final String nameFirstSpell = "§dDiscographie";
	
	public static final String nameImmortelSpell = "§4Immortel ou Mortel";
	public static final Integer timerImmortelSpell = 20;
	public static final Integer secDurationImmortelSpell = 3;
	
	public static final String namePommeSpell = "§2J'ai mangé une Pomme";
	public static final Integer timerPommeSpell = 15;
	public static final Integer secDurationEffectPommeSpell = 10;
	
	public static final String nameZoukerSpell = "§3Venez Venez Zouker";
	public static final Integer timerZoukerSpell = 20;
	public static final Integer tickDurationZoukerSpell = 50;
	public static final Double rangeZoukerSpell = 5.;
	
	public static final String nameSaiyanSpell = "§eDavid Super Saiyan";
	public static final Integer timerSaiyanSpell = 45;
	public static final Integer durationTickSaiyanSpell = 60;
	public static final Double bonusSaiyanSpell = 0.2;
	public static final Double rangeExplosionSaiyanSpell = 5.;
	public static final Double damageExplosionSaiyanSpell = 5.;
	
	public static final String namePapillonSpell = "§5Petit Papillon";
	public static final Integer timerPapillonSpell = 45;
	public static final Double damagePapillonSpell = 5.;
	
	public static final String nameSecondSpell = "§dDédicace Prioritaire";
	public static final Integer timerSecondSpell = 30;
	public static final Integer secDurationEffectSecondSpell = 10;
	public static final Double removeLifeSecondSpell = -5.;
	
	private Inventory musicInventory = null;
	private ItemStack musicActual = null; 
	
	public DJMomone(Player player) {
		super(player, Element.BOGDA, name);
		
		this.musicInventory = Bukkit.createInventory(player, 9, nameFirstSpell);
		this.musicInventory.setItem(0, getItemImmortelSpell());
		this.musicInventory.setItem(2, getItemPommeSpell());
		this.musicInventory.setItem(4, getItemZoukerSpell());
		this.musicInventory.setItem(6, getItemSaiyanSpell());
		this.musicInventory.setItem(8, getItemPapillonSpell());
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
	public Double removeDamage(Double damage, Skylander skylanderHit) { 
		if (plugin.random.nextDouble() < probaPassif) {
			player.sendMessage(Constants.prefixMessage + namePassif + "§f vient de réduire les dégats subis !");
			return damage - bonusPassif;
		}
		return damage; 
	}
	
	@Override
	public Double addDamage(Double damage, Skylander skylanderHit) {
		if (plugin.random.nextDouble() < probaPassif) {
			player.sendMessage(Constants.prefixMessage + namePassif + "§f vient de augmenter les dégats infligés !");
			return damage + bonusPassif;
		}
		return damage; 
	}
	
	@Override
	public void onStart() { 
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();

		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU5OTIxN2YwNjMyZjY5Nzk1YTVlYjc1ZjZlNWY5YjEwMTljNTM1OTFhMWI5ZTk4ODBlNGJhZWRjNDlkMDM2NSJ9fX0="));

		try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        skull.setItemMeta(meta);

        Location playerLoc = player.getLocation();
        Vector direction = playerLoc.getDirection().normalize();
        Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Location armorStandLoc = playerLoc.clone().add(right.multiply(0.7)).add(0, 1.2, 0);

        World world = player.getWorld();
        ArmorStand armorStand = world.spawn(armorStandLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.getEquipment().setHelmet(skull);
        armorStand.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));

        new BukkitRunnable() {
            private DustOptions goldDust = new DustOptions(Color.fromRGB(255, 215, 0), (float) 0.8);
            private Double angle = 0.;
			
			@Override
			public void run() {
				if (!alive || !plugin.game.isState(GameState.FIGHTING)) {
					armorStand.remove();
					cancel();
					return;
				}
				
                Location playerLoc = player.getLocation();
                Vector direction = playerLoc.getDirection().normalize();
                Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
                Location armorStandLoc = playerLoc.clone().add(right.multiply(0.7)).add(0, 1.2, 0);
                armorStandLoc.setYaw(playerLoc.getYaw());
                armorStand.teleport(armorStandLoc);
                
                if (player.isSneaking()) {
                	armorStand.getEquipment().clear();
                } else {
	                if (armorStand.getEquipment().getHelmet().getType().equals(Material.AIR)) {
		                armorStand.getEquipment().setHelmet(skull);
		                armorStand.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
	                }
	                
	                Location loc = armorStand.getLocation().add(0, 1.5, 0);

	                for (int i = 0; i < 8; i++) {
	                    double offsetX = Math.cos(angle + i * Math.PI / 4) * 0.3;
	                    double offsetZ = Math.sin(angle + i * Math.PI / 4) * 0.3;
	                    
	                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(offsetX, 0, offsetZ), 1, goldDust);
	                }

	                angle += Math.PI / 16;
	            }
			}
		}.runTaskTimer(plugin, 0, 1);
        
		return; 
	} 

	@Override
	public Boolean onKill(Skylander skylanderDeath) {
		skylanderDeath.getPlayer().playSound(skylanderDeath.getPlayer().getLocation(), "minecraft:djmomone.kill", SoundCategory.RECORDS, 1, 1);
		player.playSound(player.getLocation(), "minecraft:djmomone.kill", SoundCategory.RECORDS, 1, 1);
		return false; 
	}
	
	@Override
	public Boolean onDeath(Skylander skylanderKill) {
		skylanderKill.getPlayer().playSound(skylanderKill.getPlayer().getLocation(), "minecraft:djmomone.death", SoundCategory.RECORDS, 1, 1);
		player.playSound(player.getLocation(), "minecraft:djmomone.death", SoundCategory.RECORDS, 1, 1);
		return false; 
	} 
	
	public void firstSpell_Inventory() {
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
		player.openInventory(musicInventory);
	}
	
	public void firstSpell_Select(ItemStack item) {
		if (musicActual != null) {
			player.getInventory().remove(musicActual);
		}
		
		musicActual = item.clone();
		player.getInventory().addItem(item);
		player.sendMessage(Constants.prefixMessage + "Vous venez de choisir votre chanson "+ item.getItemMeta().getDisplayName() +"§f.");
	}
	
	public void firstSpell_Immortel() {
		if (checkCooldown(nameImmortelSpell, true)) {
			Integer idx = plugin.random.nextInt(4)+1;
			player.playSound(player.getLocation(), "minecraft:djmomone.immortel-"+idx, SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'interprêter votre chanson "+ nameImmortelSpell +"§f.");
			
			SpellUtils.invulnerability(plugin, this, secDurationImmortelSpell * 20);
			
			addCooldown(nameImmortelSpell, timerImmortelSpell);
			return;
		}
	}
	
	public void firstSpell_Pomme() {
		if (checkCooldown(namePommeSpell, true)) {
			Integer idx = plugin.random.nextInt(4)+1;
			player.playSound(player.getLocation(), "minecraft:djmomone.pomme-"+idx, SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'interprêter votre chanson "+ namePommeSpell +"§f.");
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, secDurationEffectSecondSpell * 20, 1, false, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, secDurationEffectSecondSpell * 20, 1, false, false));
			
			addCooldown(namePommeSpell, timerPommeSpell);
			return;
		}
	}
	
	public void firstSpell_Zouker() {
		if (checkCooldown(nameZoukerSpell, true)) {
			Integer idx = plugin.random.nextInt(3)+1;
			player.playSound(player.getLocation(), "minecraft:djmomone.zouker-"+idx, SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'interprêter votre chanson "+ nameZoukerSpell +"§f.");
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeZoukerSpell, 2., rangeZoukerSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.playSound(playerHit.getLocation(), "minecraft:djmomone.zouker-"+idx, SoundCategory.RECORDS, 1, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameZoukerSpell +"§f de §d"+ player.getName() +"§f.");
				playerHit.sendTitle(nameZoukerSpell, "§7Vous avez été étourdi (§d"+ SkylanderConverter.convertTicks(tickDurationZoukerSpell) +"s§7)", 1, tickDurationZoukerSpell, 1);
				skylanderHit.addStatus(tickDurationZoukerSpell, Status.NOSPELL, Status.NOMOVE, Status.NOMAKEDAMAGE);
			}
			
			addCooldown(nameZoukerSpell, timerZoukerSpell);
		}
	}
	
	public void firstSpell_Saiyan() {
		if (checkCooldown(nameSaiyanSpell, true)) {
			Integer idx = plugin.random.nextInt(4)+1;
			player.playSound(player.getLocation(), "minecraft:djmomone.saiyan-"+idx, SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'interprêter votre chanson "+ namePapillonSpell +"§f.");
			player.spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
			
			for (Skylander skylanderHit : SpellUtils.skylanderAround(plugin, this, player.getLocation(), rangeExplosionSaiyanSpell, rangeExplosionSaiyanSpell, rangeExplosionSaiyanSpell)) {
				Player playerHit = skylanderHit.getPlayer();
				playerHit.playSound(playerHit.getLocation(), "minecraft:djmomone.saiyan"+idx, SoundCategory.RECORDS, 1, 1);
				playerHit.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ nameSaiyanSpell +"§f de §d"+ player.getName() +"§f.");
				playerHit.damage(damageExplosionSaiyanSpell, player);
			}
			
			force += bonusSaiyanSpell;
			resis -= bonusSaiyanSpell;
			
			new BukkitRunnable() {
				private Integer timer = durationTickSaiyanSpell;
				
				@Override
				public void run() {
					if (timer == 0 || !alive || !plugin.game.isState(GameState.FIGHTING)) {
						force += bonusSaiyanSpell;
						resis -= bonusSaiyanSpell;
						cancel();
						return;
					}
					
					particle(player.getLocation());
					
					timer--;
				}
				
				public void particle(Location loc) {
			        Particle.DustOptions yellowDust = new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1.5f);

			        // Aura en cercle (REDSTONE + FIREWORKS_SPARK)
			        for (int i = 0; i < 20; i++) {
			            double angle = i * (Math.PI * 2 / 20);
			            double x = Math.cos(angle) * 0.8;
			            double z = Math.sin(angle) * 0.8;
			            double y = 0.5 + Math.sin(timer / 5.0 + i) * 0.3;
			            Location particleLoc = loc.clone().add(x, y, z);

			            // Jaune (Aura)
			            player.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 0, yellowDust);

			            // Étincelles
			            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 0);
			        }

			        // Traînées d'énergie ascendantes (END_ROD)
			        for (int i = 0; i < 5; i++) {
			            double x = (Math.random() - 0.5) * 1.2;
			            double z = (Math.random() - 0.5) * 1.2;
			            double y = Math.random() * 1.5;
			            Location endRodLoc = loc.clone().add(x, y, z);
			            player.getWorld().spawnParticle(Particle.END_ROD, endRodLoc, 0);
			        }

			        // Petites flammes à la base du joueur
			        for (int i = 0; i < 5; i++) {
			            double x = (Math.random() - 0.5) * 0.8;
			            double z = (Math.random() - 0.5) * 0.8;
			            Location flameLoc = loc.clone().add(x, 0.1, z);
			            player.getWorld().spawnParticle(Particle.FLAME, flameLoc, 0);
			        }
				}
				
			}.runTaskTimer(plugin, 0, 5);
			
			addCooldown(nameSaiyanSpell, timerSaiyanSpell);
			return;
		}
	}
	
	public void firstSpell_Papillon() {
		if (checkCooldown(namePapillonSpell, true)) {
			Integer idx = plugin.random.nextInt(4)+1;
			player.playSound(player.getLocation(), "minecraft:djmomone.papillon-"+idx, SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez d'interprêter votre chanson "+ namePapillonSpell +"§f.");
			
			SpellUtils.fly(
				this, 
				1.5, 
				(attacker, target) -> {
					Player playerTarget = target.getPlayer();
					playerTarget.playSound(playerTarget.getLocation(), "minecraft:djmomone.papillon"+idx, SoundCategory.RECORDS, 1, 1);
					playerTarget.sendMessage(Constants.prefixMessage + "Vous venez d'être touché par la compétence "+ namePapillonSpell +"§f de §d"+ player.getName() +"§f.");
					playerTarget.damage(damagePapillonSpell, player);
				}, 
				(location) -> {
					World world = location.getWorld();
					world.spawnParticle(Particle.CRIT, location, 10, 0.3, 0.3, 0.3, 0.1);
				}
			);
			
			addCooldown(namePapillonSpell, timerPapillonSpell);
		}
	}
	
	public void secondSpell_Book() {
		if (!checkCooldown(nameSecondSpell, true)) { player.closeInventory(); }
	}
	
	public void secondSpell_Apply(String name) {
		Skylander skylanderCheck = null;
		
		for (GamePlayer gamePlayer : plugin.game.getPlayers())
			if (gamePlayer.getSkylander().isAlive() && gamePlayer.getPlayer().getName().toLowerCase().equals(name.toLowerCase()))
				skylanderCheck = gamePlayer.getSkylander();
	
		if (skylanderCheck == null) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Aucun joueur trouvé pour le nom '§d"+ name +"'§f.");
			return;
		} else {
			Player playerCheck = skylanderCheck.getPlayer();
			
			playerCheck.playSound(playerCheck.getLocation(), "minecraft:djmomone.dedicace", SoundCategory.RECORDS, 1, 1);
			playerCheck.sendMessage(Constants.prefixMessage + "Vous avez reçu une "+ nameSecondSpell +" de §6"+ player.getName() +"§f.");
			playerCheck.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, secDurationEffectSecondSpell * 20, 0, false, false));
			playerCheck.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, secDurationEffectSecondSpell * 20, 1, false, false));
			playerCheck.addPotionEffect(new PotionEffect(PotionEffectType.POISON, secDurationEffectSecondSpell * 20, 1, false, false));
			SpellUtils.changeLife(skylanderCheck, removeLifeSecondSpell);
			
			player.playSound(player.getLocation(), "minecraft:djmomone.dedicace", SoundCategory.RECORDS, 1, 1);
			player.sendMessage(Constants.prefixMessage + "Vous venez de faire une "+ nameSecondSpell +"§f pour §6"+ playerCheck.getName() +"§f.");
			player.getInventory().setItemInMainHand(getItemSecondSpell());
									
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
		player.sendMessage("≫ "+ namePassif +"§f, vous avez §6"+ probaPassif*100 +"%§f de chance d'§aaugmenter§f de "+ bonusPassif +" §edégats infligés§f et de §aréduire§f de "+ bonusPassif +" les §edégats subis§f.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameFirstSpell + "§f, vous ouvrez un inventaire avec §e5 chansons différents§f, vous pouvez en sélectionner §cune seule à la fois§f et chaqu'elle a un §bdélai de rechargement différent§f.");
		player.sendMessage("\n");
		player.sendMessage("≫ " + nameSecondSpell + "§f, vous ouvrez un livre, si vous y écrivez le nom d'un joueur celui-ci subira les effets §2Poison§f, §7Ralentissement§f et §6Brillance§f pendant "+ secDurationEffectSecondSpell +" secondes. §b(" + timerSecondSpell + "s de recharge)");
		player.sendMessage("\n");
		player.sendMessage("===============");
		player.sendMessage("\n");
	}
	
	public static ItemStack getSignatureItem() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add("§d"+ name +"§f est un Skylander §cmélée§f ayant beaucoup de capacité différentes");
		ItemStack item = new ItemStack(Material.JUKEBOX, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§d"+name);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack getItemImmortelSpell() {
		List<String> lore = Arrays.asList(
			"§fVous devenez §einvulnérable§f pendant §6"+ secDurationImmortelSpell +" secondes§f.",
			"§b("+ timerImmortelSpell +"s de recharge)"
		);
		ItemStack it = new ItemStack(Material.MUSIC_DISC_CHIRP, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(nameImmortelSpell);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		itM.setLore(lore);
		it.setItemMeta(itM);
		return it;		
	}
	
	private static ItemStack getItemPommeSpell() {
		List<String> lore = Arrays.asList(
			"§fVous gagnez l'effet §9Vitesse§f et §dRégénération§f pendant §6"+ secDurationEffectPommeSpell +" secondes§f.",
			"§b("+ timerPommeSpell +"s de recharge)"
		);
		ItemStack it = new ItemStack(Material.MUSIC_DISC_FAR, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(namePommeSpell);
		itM.setLore(lore);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		it.setItemMeta(itM);
		return it;	
	}
	
	private static ItemStack getItemZoukerSpell() {
		List<String> lore = Arrays.asList(
			"§fVous §6étourdissez§f tout les joueurs autour de vous (- de "+ rangeZoukerSpell +" blocs).",
			"§b("+ timerZoukerSpell +"s de recharge)"
		);
		ItemStack it = new ItemStack(Material.MUSIC_DISC_WAIT, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(nameZoukerSpell);
		itM.setLore(lore);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		it.setItemMeta(itM);
		return it;		
	}
	
	private static ItemStack getItemSaiyanSpell() {
		List<String> lore = Arrays.asList(
			"§fVous creez une explosion autour de vous ce qui inflige "+ damageExplosionSaiyanSpell +" dégats aux joueurs proche (- de "+ rangeExplosionSaiyanSpell +" blocs).",
			"§fVous gagnez un bonus de "+ bonusSaiyanSpell*100 +"% de Force et Résistance pendant "+ SkylanderConverter.convertTicks(durationTickSaiyanSpell*5) +" secondes.",
			"§b("+ timerSaiyanSpell +"s de recharge)"
		);
		ItemStack it = new ItemStack(Material.MUSIC_DISC_13, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(nameSaiyanSpell);
		itM.setLore(lore);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		it.setItemMeta(itM);
		return it;	
	}
	
	private static ItemStack getItemPapillonSpell() {
		List<String> lore = Arrays.asList(
			"§fVous êtes §3propulsé dans les airs§f avec des elytras, si vous passez proche d'un joueur il subira "+ damagePapillonSpell +" dégats.",
			"§b("+ timerPapillonSpell +"s de recharge)"
		);
		ItemStack it = new ItemStack(Material.MUSIC_DISC_MALL, 1);
		ItemMeta itM = it.getItemMeta();
		itM.setDisplayName(namePapillonSpell);
		itM.setLore(lore);
		itM.setUnbreakable(true);
		itM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itM.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);		
		it.setItemMeta(itM);
		return it;	
	}

	private static ItemStack getItemFirstSpell() {
		List<String> lore = Arrays.asList(
			"§f."
		);
		ItemStack item = new ItemStack(Material.JUKEBOX, 1);
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
	
	private static ItemStack getItemSecondSpell() {
		List<String> lore = Arrays.asList(
			"§f." 
		);
		ItemStack item = new ItemStack(Material.WRITABLE_BOOK, 1);
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
	
	private static ItemStack getItemWeapon() {
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
