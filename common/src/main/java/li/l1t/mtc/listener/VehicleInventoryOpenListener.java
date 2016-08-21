/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.listener;

import li.l1t.mtc.MTC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;


public final class VehicleInventoryOpenListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvOpen(InventoryOpenEvent evt) {
        if (evt.getPlayer().isInsideVehicle()) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage(MTC.chatPrefix + "Du darfst in Vehikeln keine Inventare öffnen! (Bugusing)");
        }
    }
}
