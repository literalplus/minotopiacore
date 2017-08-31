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

import li.l1t.mtc.hook.impl.ProtocolLibHookImpl;
import org.bukkit.plugin.Plugin;

/**
 * Manages the hook for the ProtocolLib library, which allows to intercept and send packets
 * directly. This hook is only a dummy that acts solely to identify whether the library is available
 * or not.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class ProtocolLibHook extends SimpleHookWrapper {
    private ProtocolLibHookImpl unsafe;

    public ProtocolLibHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
