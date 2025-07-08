package fr.kamael.skylandersfight.skylanders.bogda.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.kamael.skylandersfight.Constants;
import fr.kamael.skylandersfight.Plugin;
import fr.kamael.skylandersfight.game.GameState;
import fr.kamael.skylandersfight.skylanders.Skylander;
import fr.kamael.skylandersfight.skylanders.Status;
import fr.kamael.skylandersfight.skylanders.bogda.Cyroule;
import fr.kamael.skylandersfight.skylanders.bogda.DJMomone;
import fr.kamael.skylandersfight.skylanders.bogda.Higrishta;
import fr.kamael.skylandersfight.skylanders.bogda.LeRosatas;
import fr.kamael.skylandersfight.skylanders.bogda.Trayyks;
import fr.kamael.skylandersfight.skylanders.bogda.ZemZem;

public class BogdaListener implements Listener {
	private Plugin plugin = Plugin.plugin;
	
	@EventHandler
	public void inventoryBogdaClick(InventoryClickEvent event) {
		try {
			if (plugin.game != null && plugin.game.isState(GameState.FIGHTING)) {				
				Player player = (Player) event.getWhoClicked();
				Skylander skylander = plugin.game.getPlayer(player).getSkylander();
				ItemStack it = event.getCurrentItem();
				
				if (it == null || it.getType().equals(Material.GRAY_STAINED_GLASS_PANE))
					return;

				if (event.getView().getTitle().equalsIgnoreCase(Cyroule.namePassif) && skylander instanceof Cyroule) {
					event.setCancelled(true);
					
					SkullMeta itM = (SkullMeta) it.getItemMeta();
					((Cyroule) skylander).passif_Apply(itM.getOwningPlayer().getPlayer());
					
					player.closeInventory();
					return;
				} else if (event.getView().getTitle().equalsIgnoreCase(ZemZem.nameFirstSpell) && skylander instanceof ZemZem) {
					event.setCancelled(true);
					
					SkullMeta itM = (SkullMeta) it.getItemMeta();
					((ZemZem) skylander).firstSpell_Apply(itM.getOwningPlayer().getPlayer());
					
					player.closeInventory();
					return;
				} else if (event.getView().getTitle().equalsIgnoreCase(DJMomone.nameFirstSpell) && skylander instanceof DJMomone) {
					event.setCancelled(true);
					
					((DJMomone) skylander).firstSpell_Select(it);
					
					player.closeInventory();
					return;
				} else if (event.getView().getTitle().equals(Trayyks.nameFirstSpell) && skylander instanceof Trayyks) {
					event.setCancelled(true);
					
					SkullMeta itM = (SkullMeta) it.getItemMeta();
					((Trayyks) skylander).firstSpell_Apply(itM.getOwningPlayer().getPlayer());
					
					player.closeInventory();
					return;
				} else if (event.getView().getTitle().equals(ZemZem.nameFirstSpell) && skylander instanceof ZemZem)
				
				return;	
			}
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(PluginListener, inventoryArenaClick) : §7"+e.getMessage());	
			return;
		}
	}
	
    @EventHandler
    public void onDJMomoneEditBook(PlayerEditBookEvent event) {
        try {
            if (plugin == null || !plugin.game.isState(GameState.FIGHTING) ||  event.isSigning() || event.isCancelled())
                return;
            
            BookMeta bookMeta = event.getNewBookMeta();
            Player player = event.getPlayer();
            Skylander skylander = plugin.game.getPlayer(player).getSkylander();
            
            if (skylander instanceof DJMomone && skylander.isAlive()) {
            	String content = bookMeta.getPage(1);
            	
            	((DJMomone) skylander).secondSpell_Apply(content.replaceAll("\\s+", ""));
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage("§c[Error]§f (PluginListener, onDJMomoneEditBook) : §7"+e.getMessage());	
		}
    }
	
	@EventHandler
	public void playerInteractBogda(PlayerInteractEvent event) {
		try {
			if (plugin.game == null || !plugin.game.isState(GameState.FIGHTING))
				return;
			
            Player player = event.getPlayer();
            Action action = event.getAction();
            ItemStack item = event.getItem();
            Skylander skylander = plugin.game.getPlayer(player).getSkylander();
            
            if (skylander.checkStatus(Status.NOSPELL) || item == null || item.getItemMeta() == null)
                return;
            
            String nameItem = item.getItemMeta().getDisplayName();

            if (skylander instanceof Higrishta) {
            	handleHigrishta((Higrishta) skylander, action, nameItem);
            } else if (skylander instanceof Cyroule) {
            	handleCyroule((Cyroule) skylander, action, nameItem);
            } else if (skylander instanceof ZemZem) {
            	handleZemZem((ZemZem) skylander, action, nameItem);
            } else if (skylander instanceof DJMomone) {
            	handleDJMomone((DJMomone) skylander, action, nameItem);
            } else if (skylander instanceof LeRosatas) {
            	handleLeRosatas((LeRosatas) skylander, action, nameItem);
            } else if (skylander instanceof Trayyks) {
            	handleTrayyks((Trayyks) skylander, action, nameItem);
            }
		}
		catch (Exception e) {
			Bukkit.broadcastMessage(Constants.prefixError + "(BogdaListener, playerInteractBogda) : §7"+e.getMessage());	
			return;
		}
	}
	
	private void handleHigrishta(Higrishta skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Higrishta.nameFirstSpell, skylander::firstSpell_Sphere);
        actions.put(Higrishta.nameSecondSpell, skylander::secondSpell_Blackhole);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleCyroule(Cyroule skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Cyroule.namePassif, skylander::passif_Inventory);
        actions.put(Cyroule.nameFirstSpell, skylander::firstSpell_NoAttack);
        actions.put(Cyroule.nameSecondSpell, skylander::secondSpell_Arena);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleZemZem(ZemZem skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(ZemZem.nameFirstSpell, skylander::firstSpell_Inventory);
        actions.put(ZemZem.nameSecondSpell, skylander::secondSpell_Teleport);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleDJMomone(DJMomone skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(DJMomone.nameImmortelSpell, skylander::firstSpell_Immortel);
        actions.put(DJMomone.namePommeSpell, skylander::firstSpell_Pomme);
        actions.put(DJMomone.nameZoukerSpell, skylander::firstSpell_Zouker);
        actions.put(DJMomone.nameSaiyanSpell, skylander::firstSpell_Saiyan);
        actions.put(DJMomone.namePapillonSpell, skylander::firstSpell_Papillon);
        actions.put(DJMomone.nameFirstSpell, skylander::firstSpell_Inventory);
        actions.put(DJMomone.nameSecondSpell, skylander::secondSpell_Book);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleLeRosatas(LeRosatas skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(LeRosatas.nameFirstSpell, skylander::firstSpell_TimeRewind);
        actions.put(LeRosatas.nameSecondSpell, skylander::secondSpell_TimeRoot);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
	private void handleTrayyks(Trayyks skylander, Action action, String name) {
        Map<String, Runnable> actions = new HashMap<>();
        actions.put(Trayyks.nameFirstSpell, skylander::firstSpell_Inventory);
        actions.put(Trayyks.nameSecondSpellAir, skylander::secondSpell_Air);
        actions.put(Trayyks.nameSecondSpellBogda, skylander::secondSpell_Bogda);
        actions.put(Trayyks.nameSecondSpellEau, skylander::secondSpell_Eau);
        actions.put(Trayyks.nameSecondSpellFeu, skylander::secondSpell_Feu);
        actions.put(Trayyks.nameSecondSpellMagie, skylander::secondSpell_Magie);
        actions.put(Trayyks.nameSecondSpellMort, skylander::secondSpell_Mort);
        actions.put(Trayyks.nameSecondSpellTech, skylander::secondSpell_Tech);
        actions.put(Trayyks.nameSecondSpellTerre, skylander::secondSpell_Terre);        
        actions.put(Trayyks.nameSecondSpellVie, skylander::secondSpell_Vie);

        if (isRightClick(action) && actions.containsKey(name)) {
        	actions.get(name).run();
        }
	}
	
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
