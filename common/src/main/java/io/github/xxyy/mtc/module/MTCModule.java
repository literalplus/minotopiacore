/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.CacheHelper;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a MTC module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public interface MTCModule extends CacheHelper.Cache {
    /**
     * Enables this module.
     *
     * @param plugin the plugin managing your module today
     * @throws Exception thrown if anything fails, meaning the module did not get enabled
     */
    void enable(MTC plugin) throws Exception;

    /**
     * Disables this module. This does not allow exceptions since plugins should disable when asked and not be able to
     * put requirements.
     *
     * @param plugin the plugin managing your module, again
     */
    void disable(MTC plugin);

    /**
     * Reloads this module's configurations and related facilities.
     *
     * @param plugin the plugin issuing the reload
     */
    void reload(MTC plugin);

    /**
     * @return whether this module is currently enabled
     */
    boolean isEnabled();

    /**
     * @return a hopefully unique and definitely static name describing this module
     */
    String getName();

    /**
     * Checks whether this plugin can be enabled in the current environment. The default implementation checks for a
     * user-defined switch in the global configuration file.
     *
     * @param plugin the plugin requesting the state
     * @return whether this plugin wants to be enabled (modules have feelings, too)
     * @see ModuleManager#isEnabled(MTCModule)
     */
    boolean canBeEnabled(MTC plugin);

    /**
     * @return the plugin associated with this module
     */
    JavaPlugin getPlugin();
}
