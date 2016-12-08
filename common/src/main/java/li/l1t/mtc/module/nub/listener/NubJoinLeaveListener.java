/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.listener;

import li.l1t.mtc.module.nub.api.ProtectionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listens for join and leave events and forwards them to the protection service.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class NubJoinLeaveListener implements Listener {
    private final ProtectionService service;
    private final Plugin plugin;

    public NubJoinLeaveListener(ProtectionService service, Plugin plugin) {
        this.service = service;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> startProtectionSyncIfEligible(event.getPlayer())
        );
    }

    private void startProtectionSyncIfEligible(Player player) {
        if (service.isEligibleForProtection(player)) {
            startProtectionSync(player);
        }
    }

    private void startProtectionSync(Player player) {
        plugin.getServer().getScheduler().runTask(plugin,
                () -> service.startProtection(player)
        );
    }
}
