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

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;
import org.bukkit.Location;

import java.util.Map;

/**
 * Unsafe implementation of the WorldGuard hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class WorldGuardHookImpl implements Hook {
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "WorldGuard");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        hooked = WGBukkit.getPlugin() != null; //Re-ensure that the class is loaded
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    public boolean isPvP(Location loc) {
        ApplicableRegionSet applicableRegions =
                WGBukkit.getRegionManager(loc.getWorld())
                        .getApplicableRegions(loc);

        if (applicableRegions.size() == 1) {
            ProtectedRegion protectedRegion = applicableRegions.getRegions().stream().findFirst().get();
            if (protectedRegion.getId().equals(ProtectedRegion.GLOBAL_REGION)) {
                Map<Flag<?>, Object> flags = protectedRegion.getFlags();
                if (!flags.containsKey(DefaultFlag.PVP)) {
                    return true;
                }
            }
        }
        StateFlag.State pvpState = applicableRegions.queryState(null, DefaultFlag.PVP);
        return pvpState != StateFlag.State.DENY;
    }
}
