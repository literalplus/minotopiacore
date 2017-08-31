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

import li.l1t.mtc.hook.impl.TitleManagerHookImpl;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Manages the hook for the TitleManager plugin, which allows to show titles and actionbar messages
 * from code.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class TitleManagerHook extends SimpleHookWrapper {
    private TitleManagerHookImpl unsafe;

    public TitleManagerHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public void sendTitle(Player plr, String title, String subtitle) {
        if (!isActive()) {
            return;
        }

        unsafe.sendTitle(plr, title, subtitle);
    }

    public void sendActionbarMessage(Player plr, String message) {
        if (!isActive()) {
            return;
        }

        unsafe.sendActionbarMessage(plr, message);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
