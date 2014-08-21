package io.github.xxyy.mtc.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public final class MinecartPortalListener implements Listener {
	@EventHandler
	public void onPortal(EntityPortalEvent e){
		if(e.getEntityType() == EntityType.MINECART_CHEST || e.getEntityType() == EntityType.MINECART_FURNACE || e.getEntityType() == EntityType.MINECART_HOPPER){
			e.setCancelled(true);
			System.out.println("Minecart tried to travel through portal...DENIED!");
		}
	}
}
