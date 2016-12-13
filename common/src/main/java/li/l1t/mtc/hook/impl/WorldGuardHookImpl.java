/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
