/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.website;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to join and quit events for the website module to help persist players' play time.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11/10/14
 */
final class WebsiteListener implements Listener {
    private final WebsiteModule module;

    WebsiteListener(WebsiteModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent evt) {
        module.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(module.getPlugin(), () -> {
            module.setPlayerOnline(evt.getPlayer(), true);
            module.registerJoinTime(evt.getPlayer().getUniqueId());
        }, 10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent evt) {
        module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(), () -> { //Let's remove this player from the table of online players and save their newly acquired play time
            module.setPlayerOnline(evt.getPlayer(), false);
            module.saveTimePlayed(evt.getPlayer().getUniqueId());
        });
    }
}
