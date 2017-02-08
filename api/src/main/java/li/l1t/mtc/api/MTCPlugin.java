/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api;

import li.l1t.common.sql.SpigotSql;
import li.l1t.common.xyplugin.XyPluggable;
import li.l1t.mtc.api.module.ModuleManager;
import li.l1t.mtc.api.module.inject.Injectable;
import org.bukkit.plugin.Plugin;

/**
 * A plugin instance used by MTC to interface with the Bukkit API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public interface MTCPlugin extends Plugin, XyPluggable, Injectable {

    /**
     * @return the module manager used by this plugin
     */
    ModuleManager getModuleManager();

    /**
     * @return the database manager used by this plugin
     */
    SpigotSql getSql();

    /**
     * Convenience shorthand for scheduling an asynchronous task with the server's scheduler for immediate execution.
     *
     * @param task the task to execute
     */
    void async(Runnable task);
}
