/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;


public final class ColoredSignListener implements Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onSignChange(SignChangeEvent e) {
        if (e.getPlayer().hasPermission("mtc.signcolor.all")) {
            for (int i = 0; i <= 3; i++) {
                e.setLine(i, ChatColor.translateAlternateColorCodes('&', e.getLine(i)));
            }
        }
    }
}
