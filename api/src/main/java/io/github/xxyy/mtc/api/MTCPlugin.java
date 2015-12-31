package io.github.xxyy.mtc.api;

import io.github.xxyy.common.xyplugin.XyPluggable;
import io.github.xxyy.mtc.api.module.ModuleManager;
import org.bukkit.plugin.Plugin;

/**
 * A plugin instance used by MTC to interface with the Bukkit API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public interface MTCPlugin extends Plugin, XyPluggable {

    /**
     * @return the module manager used by this plugin
     */
    ModuleManager getModuleManager();
}
