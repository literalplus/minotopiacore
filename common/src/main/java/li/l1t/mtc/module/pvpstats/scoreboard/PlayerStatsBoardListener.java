/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
