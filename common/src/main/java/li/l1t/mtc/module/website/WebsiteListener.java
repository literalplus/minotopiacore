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

package li.l1t.mtc.module.website;

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
