/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.pvpstats.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens to events related to the PvP Stats Scoreboard.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-05
 */
public class PvPStatsBoardListener implements Listener {
    private final PvPStatsBoardManager manager;

    public PvPStatsBoardListener(PvPStatsBoardManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent evt) {
        Player victim = evt.getEntity();
        manager.updateScoreboard(evt.getEntity());
        if (victim.getKiller() != null) {
            manager.updateScoreboard(evt.getEntity().getKiller());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        manager.getModule().getPlugin().getServer().getScheduler().runTaskAsynchronously(
                manager.getModule().getPlugin(),
                () -> manager.updateScoreboard(evt.getPlayer())
        ); //May make a database call + ProtocolLib is async save
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(PlayerKickEvent evt) {
        manager.cleanUp(evt.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent evt) {
        manager.cleanUp(evt.getPlayer());
    }
}
