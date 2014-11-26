/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook;

import io.github.xxyy.mtc.hook.impl.WorldGuardHookImpl;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Hooks into WorldGuard.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class WorldGuardHook extends SimpleHookWrapper {
    private WorldGuardHookImpl unsafe;

    public WorldGuardHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    /**
     * Checks whether there are any WorldGuard regions blocking PvP at the given location.
     *
     * @param loc Location to check
     * @return FALSE if PvP is blocked at that location, TRUE if PvP is allowed or WorldGuard is not installed.
     */
    public boolean isPvP(Location loc) {
        return !isActive() || unsafe.isPvP(loc);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
