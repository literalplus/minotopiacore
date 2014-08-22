package io.github.xxyy.mtc.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;


public final class MainInventoryOpenListener implements Listener {
    @EventHandler
    public void onInvOpen(InventoryOpenEvent evt){
        if(evt.getPlayer().isInsideVehicle()){
            evt.setCancelled(true);
            ((Player) evt.getPlayer()).sendMessage("§c[MTC]§8 Du darfst in Vehikeln keine Inventare öffnen! (Bugusing)");
        }
    }
}
