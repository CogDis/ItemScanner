package com.precipicegames.itemscanner;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.InventoryComponent;
import com.sk89q.commandbook.components.AbstractComponent;
import com.sk89q.minecraft.util.commands.CommandException;


public class ItemScanner extends JavaPlugin implements Listener {

	private InventoryComponent ibook;

	public void onDisable() {
		
	}

	public void onEnable() {
		CommandBook book = CommandBook.inst();
		AbstractComponent component = book.getComponentManager().getComponent("items");
		if(component instanceof InventoryComponent) {
			ibook = (InventoryComponent) component;
			this.getServer().getPluginManager().registerEvents(this, this);
		} else {
			System.out.println(this + ": Your commandbook appears to be without the item component");
		}
	}
	
	@EventHandler
	public void onPlayerInventory(PlayerInventoryEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			return;
		}
		this.scanInventory(event.getInventory(), event.getPlayer());
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
			try {
				ibook.checkAllowedItem(p, i.getTypeId());
			} catch (CommandException e) {
				allowed = false;
			}
			if(!(allowed || p.hasPermission("itemscanner.bypass." + i.getTypeId()))) {
				inv.remove(i);
			}
		}
	}
}
