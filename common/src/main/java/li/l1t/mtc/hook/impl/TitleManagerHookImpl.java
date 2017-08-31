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

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;
import org.bukkit.entity.Player;

/**
 * Unsafe implementation of the TitleManager hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class TitleManagerHookImpl implements Hook {
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "TitleManager");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        hooked = TitleObject.class.getName() != null; //Make sure the class is loaded
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    public void sendTitle(Player plr, String title, String subtitle) {
        new TitleObject(title, subtitle)
                .send(plr);
    }

    public void sendActionbarMessage(Player plr, String message) {
        new ActionbarTitleObject(message)
                .send(plr);
    }
}
