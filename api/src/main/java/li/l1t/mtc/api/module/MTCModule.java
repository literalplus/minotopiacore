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

package li.l1t.mtc.api.module;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.misc.Cache;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a MTC module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public interface MTCModule extends Cache {
    /**
     * Enables this module.
     *
     * @param plugin the plugin managing your module today
     * @throws Exception thrown if anything fails, meaning the module did not get enabled
     */
    void enable(MTCPlugin plugin) throws Exception;

    /**
     * Disables this module. This does not allow exceptions since plugins should disable when asked
     * and not be able to put requirements.
     *
     * @param plugin the plugin managing your module, again
     */
    void disable(MTCPlugin plugin);

    /**
     * Reloads this module's configurations and related facilities.
     *
     * @param plugin the plugin issuing the reload
     */
    void reload(MTCPlugin plugin);

    /**
     * @return whether this module is currently enabled
     */
    boolean isEnabled();

    /**
     * @return a hopefully unique and definitely static name describing this module
     */
    String getName();

    /**
     * Checks whether this plugin can be enabled in the current environment. The default
     * implementation checks for a user-defined switch in the global configuration file.
     *
     * @param plugin the plugin requesting the state
     * @return whether this plugin wants to be enabled (modules have feelings, too)
     * @see ModuleManager#isEnabled(MTCModule)
     */
    boolean canBeEnabled(MTCPlugin plugin);

    /**
     * @return the plugin associated with this module
     */
    JavaPlugin getPlugin();

    /**
     * @return the prefix to be used by messages sent by this module
     */
    String getChatPrefix();
}
