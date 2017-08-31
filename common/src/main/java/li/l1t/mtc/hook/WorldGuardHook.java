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

import li.l1t.mtc.hook.impl.WorldGuardHookImpl;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Hooks into WorldGuard.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class WorldGuardHook extends SimpleHookWrapper {
    private WorldGuardHookImpl unsafe;

    public WorldGuardHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    /**
     * Checks whether there are any WorldGuard regions blocking PvP at the given location.
     *
     * @param loc Location to check
     * @return FALSE if PvP is blocked at that location, TRUE if PvP is allowed or WorldGuard is not
     * installed.
     */
    public boolean isPvP(Location loc) {
        return !isActive() || unsafe.isPvP(loc);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
