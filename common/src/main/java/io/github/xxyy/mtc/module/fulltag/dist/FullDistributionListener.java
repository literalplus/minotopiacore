/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag.dist;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Listens for player join events in order to guarantee that full distribution attempts are continued even if the
 * target player leaves the game.
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
