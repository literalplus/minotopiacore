/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

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
