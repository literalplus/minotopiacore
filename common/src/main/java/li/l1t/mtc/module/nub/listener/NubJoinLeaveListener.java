/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.nub.listener;

import li.l1t.mtc.module.nub.api.ProtectionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
                () -> service.startOrResumeProtection(player)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(service.hasProtection(player)) {
            service.pauseProtection(player);
        }
    }
}
