package io.github.xxyy.minotopiacore.hook.impl;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Location;

import io.github.xxyy.minotopiacore.hook.HookWrapper;
import io.github.xxyy.minotopiacore.hook.Hooks;

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
