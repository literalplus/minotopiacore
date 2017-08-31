/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.api;

import li.l1t.common.sql.SpigotSql;
import li.l1t.common.sql.sane.SqlConnected;
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
public interface MTCPlugin extends Plugin, XyPluggable, Injectable, SqlConnected {

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
     * @see #serverThread(Runnable) (Runnable) for scheduling in the server thread
     */
    void async(Runnable task);

    /**
     * Convenience shorthand for scheduling an task with the server's scheduler for immediate execution in the server
     * thread.
     *
     * @param task the task to execute
     * @see #async(Runnable) for async scheduling
     */
    void serverThread(Runnable task);
}
