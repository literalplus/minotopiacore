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

import com.google.common.collect.ImmutableList;
import li.l1t.mtc.hook.impl.PexHookImpl;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

/**
 * Helps interfacing with PermissionsEx.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class PexHook extends SimpleHookWrapper {
    private PexHookImpl unsafe;

    public PexHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public List<PexHook.Group> getGroupList() {
        if (!isActive()) {
            this.getPlugin().getLogger().info("Could not find PermissionsEx groups because not active!");
            return ImmutableList.of();
        }

        return unsafe.getGroupList();
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }

    public interface User {
        String getIdentifier();

        boolean hasUniqueId();

        UUID getUniqueId();

        String getName();
    }

    public interface Group {
        String getName();

        String getPrefix();

        List<User> getUsers();

        boolean getOptionBoolean(String name, String world, boolean def);

        int getOptionInteger(String name, String world, int def);
    }
}
