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

package li.l1t.mtc.hook;

import org.bukkit.plugin.Plugin;

/**
 * A simple implementation of the HookWrapper interface without any actual functionality.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class SimpleHookWrapper implements HookWrapper {
    private final Plugin plugin;
    private boolean active;

    public SimpleHookWrapper(Plugin plugin, boolean active) {
        this.plugin = plugin;
        this.active = active;
    }

    public SimpleHookWrapper(Plugin plugin) {
        this(plugin, true);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }
}
