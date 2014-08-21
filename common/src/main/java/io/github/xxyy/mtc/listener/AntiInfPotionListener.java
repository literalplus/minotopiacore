package io.github.xxyy.mtc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class AntiInfPotionListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDrop(final PlayerDropItemEvent e) {
	  final ItemStack item = e.getItemDrop().getItemStack().clone();
	  	if (item.getAmount() > 64){
		    item.setAmount(64);
		    e.getItemDrop().setItemStack(item);
	  	}
	}
}
