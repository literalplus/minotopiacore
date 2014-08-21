package io.github.xxyy.mtc.hook;

import org.bukkit.plugin.Plugin;

/**
 * Common interface for hook wrappers
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public interface HookWrapper {
    boolean isActive();
    Plugin getPlugin();
}
