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

package li.l1t.mtc.hook.impl;

import com.comphenix.protocol.ProtocolLibrary;
import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;

/**
 * Unsafe implementation of the ProtocolLib hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class ProtocolLibHookImpl implements Hook {
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "ProtocolLib");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        hooked = ProtocolLibrary.class.getName() != null; //Make sure the class is loaded
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }
}
