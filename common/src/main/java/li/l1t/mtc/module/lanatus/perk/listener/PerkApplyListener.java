/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.listener;

import li.l1t.mtc.module.lanatus.perk.LocalPerkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Listens for relevant events and applies all perks a player has enabled.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PerkApplyListener implements Listener {
    private final LocalPerkManager manager;

    public PerkApplyListener(LocalPerkManager manager) {
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoinApplyPerks(PlayerJoinEvent event) {
        manager.applyEnabled(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        reapplyPerks(event);
    }

    private void reapplyPerks(PlayerEvent event) {
        manager.removeAll(event.getPlayer());
        manager.applyEnabled(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        manager.removeAll(event.getPlayer());
    }
}
