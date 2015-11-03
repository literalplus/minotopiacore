package io.github.xxyy.mtc.hook;

import org.bukkit.plugin.Plugin;

/**
 * Common interface for plugin hook contract declarations. Extensions provide API methods for interfacing with another
 * API safely. Backend calls may lead to nasty stuff like ClassNotFoundExceptions that implementations provide a
 * safe wrapper around.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public interface MTCHook {

    /**
     * Attempts to determine if the target of this hook is currently available. If this returns true, it is safe to
     * assume that API calls will not fail because of the target not being present. Note that this method may return
     * false due to the hook not being initialised yet.
     *
     * @param plugin a Bukkit plugin instance this method needs for its investigations
     * @return whether it is guaranteed with high certainty that the target is available
     */
    boolean isAvailable(Plugin plugin);

    /**
     * Attempts to initialise this hook. Note that this is a one-time operation and while it might be safe to execute
     * multiple times, no guarantees are made on the behaviour when calling this and the hook is already initialised.
     *
     * @param plugin a Bukkit plugin instance this method needs for its hooking business
     * @throws Exception            who knows what might happen
     * @throws NoClassDefFoundError if any required class is not loaded
     */
    void hook(Plugin plugin) throws Exception, NoClassDefFoundError;
}
