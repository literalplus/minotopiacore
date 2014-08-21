package io.github.xxyy.mtc.misc;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the MTC cache is cleared. Dirty workaround for storing caches in Listener instances.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.6.14
 */
public class ClearCacheEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
