package io.github.xxyy.minotopiacore.hook.impl;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import io.github.xxyy.minotopiacore.hook.WorldGuardHook;
import org.bukkit.Location;

/**
 * Unsafe implementation of the WorldGuard hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class WorldGuardHookImpl {

    public WorldGuardHookImpl(WorldGuardHook wrapper) {

    }

    public boolean isPvP(Location loc) {
        return WGBukkit
                .getRegionManager(loc.getWorld())
                .getApplicableRegions(loc)
                .allows(DefaultFlag.PVP);
    }
}
