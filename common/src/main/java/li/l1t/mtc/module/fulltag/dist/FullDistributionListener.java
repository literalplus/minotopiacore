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

package li.l1t.mtc.module.fulltag.dist;

import li.l1t.mtc.MTC;
import li.l1t.mtc.module.fulltag.model.FullInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Listens for player join events in order to guarantee that full distribution attempts are
 * continued even if the target player leaves the game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 10/09/15
 */
public class FullDistributionListener implements Listener {
    @Nonnull
    private final FullDistributionManager manager;

    public FullDistributionListener(@Nonnull FullDistributionManager manager) {
        this.manager = manager;
        manager.getModule().getPlugin().getServer().getPluginManager().registerEvents(this, manager.getModule().getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(@Nonnull PlayerJoinEvent evt) {
        MTC plugin = manager.getModule().getPlugin();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Set<Integer> queuedFullIds = manager.getQueuedFullIds(evt.getPlayer().getUniqueId());

            if (!queuedFullIds.isEmpty()) {
                List<FullInfo> fullInfos = queuedFullIds.stream()
                        .map(manager.getModule().getRegistry()::getById)
                        .collect(Collectors.toList());

                plugin.getServer().getScheduler().runTask(plugin, () -> { //save data to manager cache
                            fullInfos.forEach((info) -> manager.requestStore(info, evt.getPlayer()));
                            manager.notifyWaiting(evt.getPlayer());
                        }
                );
            }
        });
    }
}
