/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook.impl;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import io.github.xxyy.mtc.hook.HookWrapper;
import io.github.xxyy.mtc.hook.Hooks;
import org.bukkit.Location;

/**
 * Unsafe implementation of the WorldGuard hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class WorldGuardHookImpl implements Hook{
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
        return WGBukkit
                .getRegionManager(loc.getWorld())
                .getApplicableRegions(loc)
                .allows(DefaultFlag.PVP);
    }
}
