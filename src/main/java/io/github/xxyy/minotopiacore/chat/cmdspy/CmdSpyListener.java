package io.github.xxyy.minotopiacore.chat.cmdspy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Notifies subscribers of commandspy events.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 20.6.14
 */
public class CmdSpyListener implements Listener {
    @EventHandler(priority = org.bukkit.event.EventPriority.MONITOR, ignoreCancelled = true)
    public void onCmdSpy(PlayerCommandPreprocessEvent evt) {
        String cmd = evt.getMessage().substring(1, evt.getMessage().length());

        CommandSpyFilters.getActiveFilters().stream()
                .anyMatch(filter -> filter.notifyOnMatch(cmd, evt.getPlayer()));
    }
}
