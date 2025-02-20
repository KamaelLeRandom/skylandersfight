package fr.kamael.skylandersfight.game.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.utils.manager.ItemManager;

public class Config {
	private Inventory inventoryConfig;
	private Integer nbPointWin = 3;
	private Integer nbLifebar = 5;
	private Integer nbSkylanderLine = 4;
	private Integer timerDM = 5;
	private Boolean activeTeam = false;
	private Boolean activeHeal = true;
	private Boolean activeDeathmatch = true;
	private Boolean activeItem = true;
	private Boolean activeBonusMap = true;
	private Boolean activeEventMap = true;
	
	public Config() {
		Inventory inv = Bukkit.createInventory(null, 36, Constants.inventoryConfigName);
		for (Integer i = 0; i < 9; i++) {
			inv.setItem(i     , ItemManager.getInventoryGlass());
			inv.setItem(i + 18, ItemManager.getInventoryGlass());
		}
		inv.setItem( 4, ItemManager.makeBasicItem(Material.SLIME_BALL, "§aCommencer", 1));
		inv.setItem( 9, ItemManager.makeBasicItem(Material.OAK_SIGN, "§7Nombre de ligne de Skylanders", this.nbSkylanderLine));
		inv.setItem(10, ItemManager.makeBasicItem(Material.OAK_SIGN, "§7Nombre de point", this.nbPointWin));
		inv.setItem(11, ItemManager.makeBasicItem(Material.OAK_SIGN, "§7Nombre de barre de vie", this.nbLifebar));
		inv.setItem(12, ItemManager.makeBasicItem(Material.OAK_SIGN, "§7Durée avant le Deathmatch", this.timerDM));
		inv.setItem(18, ItemManager.makeBasicItem(Material.RED_TERRACOTTA, "§7Équipes", 1));
		inv.setItem(19, ItemManager.makeBasicItem(Material.GREEN_TERRACOTTA, "§7Bloc de soin", 1));
		inv.setItem(20, ItemManager.makeBasicItem(Material.GREEN_TERRACOTTA, "§7Deathmatch", 1));
		inv.setItem(21, ItemManager.makeBasicItem(Material.GREEN_TERRACOTTA, "§7Objets Aléatoire", 1));
		inv.setItem(22, ItemManager.makeBasicItem(Material.GREEN_TERRACOTTA, "§7Bonus élémentaire", 1));
		inv.setItem(23, ItemManager.makeBasicItem(Material.GREEN_TERRACOTTA, "§7Événement d'Arène", 1));
		
		this.inventoryConfig = inv;
	}
	
	public Inventory getInventory() {
		return this.inventoryConfig;
	}
	
	public Integer getNbPointWin() {
		return this.nbPointWin;
	}
	
	public Integer updateNbPointWin(InventoryAction action) {
		switch (action) {
			case PICKUP_ALL: {
				if (this.nbPointWin > 1)
					this.nbPointWin--;
				break;
			}
			case PICKUP_HALF: {
				if (this.nbPointWin < 10)
					this.nbPointWin++;
				break;
			}
			default:
				break;
		}
		return this.nbPointWin;
	}
	
	public Integer getNbLifebar() {
		return this.nbLifebar;
	}
	
	public Integer updateNbLifebar(InventoryAction action) {
		switch (action) {
			case PICKUP_ALL: {
				if (this.nbLifebar > 1)
					this.nbLifebar--;
				break;
			}
			case PICKUP_HALF: {
				if (this.nbLifebar < 5)
					this.nbLifebar++;
				break;
			}
			default:
				break;
		}
		return this.nbLifebar;
	}

	public Integer getNbSkylanderLine() {
		return this.nbSkylanderLine;
	}
	
	public Integer updateNbSkylanderLine(InventoryAction action) {
		switch (action) {
			case PICKUP_ALL: {
				if (this.nbSkylanderLine > 1)
					this.nbSkylanderLine--;
				break;
			}
			case PICKUP_HALF: {
				if (this.nbSkylanderLine < 4)
					this.nbSkylanderLine++;
				break;
			}
			default:
				break;
		}
		return this.nbSkylanderLine;
	}
	
	public Integer getTimerDM() {
		return this.timerDM;
	}
	
	public Integer updateTimerDM(InventoryAction action) {
		switch (action) {
			case PICKUP_ALL: {
				if (this.timerDM > 3)
					this.timerDM--;
				break;
			}
			case PICKUP_HALF: {
				if (this.timerDM < 10)
					this.timerDM++;
				break;
			}
			default:
				break;
		}
		return this.timerDM;
	}
	
	public Boolean getActiveTeam() {
		return this.activeTeam;
	}
	
	public void updateActiveTeam(ItemStack item) {
		this.activeTeam = !this.activeTeam;
		
		if (this.activeHeal) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}

	public Boolean getActiveHeal() {
		return this.activeHeal;
	}
	
	public void updateActiveHeal(ItemStack item) {
		this.activeHeal = !this.activeHeal;
		
		if (this.activeHeal) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}

	public Boolean getActiveDeathmatch() {
		return this.activeDeathmatch;
	}
	
	public void updateActiveDeathmatch(ItemStack item) {
		this.activeDeathmatch = !this.activeDeathmatch;
		
		if (this.activeDeathmatch) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}
	
	public Boolean getActiveItem() {
		return this.activeItem;
	}
	
	public void updateActiveItem(ItemStack item) {
		this.activeItem = !this.activeItem;
		
		if (this.activeItem) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}
	
	public Boolean getActiveBonusMap() {
		return this.activeBonusMap;
	}
	
	public void updateActiveBonusMap(ItemStack item) {
		this.activeBonusMap = !this.activeBonusMap;
		
		if (this.activeBonusMap) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}

	public Boolean getActiveEventMap() {
		return this.activeEventMap;
	}
	
	public void updateActiveEventMap(ItemStack item) {
		this.activeEventMap = !this.activeEventMap;
		
		if (this.activeEventMap) {
			item.setType(Material.GREEN_TERRACOTTA);
		} else {
			item.setType(Material.RED_TERRACOTTA);
		}
		
		return;
	}
}
