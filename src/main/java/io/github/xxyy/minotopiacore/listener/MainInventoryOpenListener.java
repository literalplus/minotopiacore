package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.helper.MTCHelper;

import java.util.List;

import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;


public class MainInventoryOpenListener implements Listener {
    @EventHandler
    public static void onInvOpen(InventoryOpenEvent evt){
        if(evt.getPlayer().isInsideVehicle()){
            evt.setCancelled(true);
            ((Player) evt.getPlayer()).sendMessage("§c[MTC]§8 Du darfst in Vehikeln keine Inventare öffnen! (Bugusing)");
            return;
        }
        InventoryHolder hldr = evt.getInventory().getHolder();
        if(hldr instanceof Dispenser){
            Dispenser disp = (Dispenser)hldr;
            List<MetadataValue> metaData = disp.getMetadata("mtc.infinite");
            for(MetadataValue val : metaData){
                if(val.getOwningPlugin().equals(MTC.instance())){
                    evt.setCancelled(true);
                    MTCHelper.sendLoc("XC-infdispclk", (Player)evt.getPlayer(), true);
                    return;
                }
            }
        }
    }
}
