package io.github.xxyy.mtc.module.website;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.xxyy.mtc.MTC;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listens to join and quit events for the website module to help persist players' play time.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 11/10/14
 */
final class WebsiteListener implements Listener {
    private final MTC plugin;
    private final Map<UUID, Instant> playerJoinTimes = new HashMap<>();

    WebsiteListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent evt) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> { //Let's insert this player into the table of online players
            plugin.getSql().safelyExecuteUpdate("INSERT INTO " + WebsiteModule.ONLINE_TABLE_NAME +
                            " SET uuid=?, name=? ON DUPLICATE KEY UPDATE name=?",        //Under certain conditions, an entry might still be there - consider
                    evt.getPlayer().getUniqueId().toString(), evt.getPlayer().getName(), //the player switching servers using BungeeCord and the other
                    evt.getPlayer().getName());                                          //server not having executed the SQL yet or a crash.
            playerJoinTimes.put(evt.getPlayer().getUniqueId(), Instant.now());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent evt) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> { //Let's remove this player from the table of online players and save their newly acquired play time
            plugin.getSql().safelyExecuteUpdate("DELETE FROM " + WebsiteModule.ONLINE_TABLE_NAME +
                    " WHERE uuid=?", evt.getPlayer().getUniqueId().toString());

            long newlyPlayedMinutes = ChronoUnit.MINUTES.between(
                    playerJoinTimes.getOrDefault(evt.getPlayer().getUniqueId(), Instant.now()), //Just making sure
                    Instant.now());

            plugin.getSql().safelyExecuteUpdate("INSERT INTO " + WebsiteModule.PLAYTIME_TABLE_NAME +
                            " SET uuid=?,minutes=? ON DUPLICATE KEY UPDATE minutes=minutes+?",
                    evt.getPlayer().getUniqueId().toString(), newlyPlayedMinutes, newlyPlayedMinutes);
        });
    }
}
