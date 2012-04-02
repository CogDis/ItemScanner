package com.precipicegames.itemscanner;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.InventoryComponent;
import com.sk89q.minecraft.util.commands.CommandException;


public class ItemScanner extends JavaPlugin implements Listener {

	private CommandBook book;
	private ItemScanner plugin;
	
	private class FindCBook implements Runnable {
		public void run() {
			Plugin p = getServer().getPluginManager().getPlugin("CommandBook");
			if(p instanceof CommandBook) {
				book = (CommandBook)p;
			} else {
				System.out.println(this + "CommandBook was not found");
				return;
			}
		}
	}
	public void onDisable() {
		
	}

	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new FindCBook());
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			return;
		}
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			switch(event.getClickedBlock().getTypeId()) {
			case 54:
			case 61:
			case 62:
			case 23:
			case 117:
				//ContainerBlock cblock = (ContainerBlock) event.getClickedBlock();
				//this.scanInventory(cblock.getInventory(), event.getPlayer());
				this.scanInventory(event.getPlayer().getInventory(), event.getPlayer());
				break;
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if(event.isCancelled())
			return;
		this.scanInventory(event.getPlayer().getInventory(), event.getPlayer());
	}
	
	public void scanInventory(Inventory inv, Player p) {
		if(p.hasPermission("itemscanner.bypass")) {
			return;
		}
		for(ItemStack  i : inv.getContents()) {
			boolean allowed = true;
			if(i == null || i.getTypeId() == 0) {
				continue;
			}
			try {
				InventoryComponent comp = book.getComponentManager().getComponent(InventoryComponent.class);
				if(comp != null) {
					comp.checkAllowedItem(p, i.getTypeId());
				}
			} catch (CommandException e) {
				allowed = false;
			}
			if(!(allowed || p.hasPermission("itemscanner.bypass." + i.getTypeId()))) {
				inv.remove(i);
			}
		}
	}
}
