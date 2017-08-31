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

package li.l1t.mtc.module.pvpstats.scoreboard;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listens to events related to the PvP Stats Scoreboard.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-05
 */
public class PlayerStatsBoardListener implements Listener {
    private final PlayerStatsBoardManager scoreboard;
    private final Plugin plugin;

    @InjectMe
    public PlayerStatsBoardListener(PlayerStatsBoardManager scoreboard, MTCPlugin plugin) {
        this.scoreboard = scoreboard;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            scoreboard.updateAll(evt.getEntity().getKiller());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> scoreboard.updateAll(event.getPlayer())
        ); //May make a database call + ProtocolLib is async save
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> scoreboard.updateAll(evt.getPlayer())
        ); //May make a database call + ProtocolLib is async save
    }
}
