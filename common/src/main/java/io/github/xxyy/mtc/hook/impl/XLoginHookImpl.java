package io.github.xxyy.mtc.hook.impl;

import io.github.xxyy.mtc.hook.HookWrapper;
import io.github.xxyy.mtc.hook.Hooks;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import io.github.xxyy.xlogin.common.PreferencesHolder;
import io.github.xxyy.xlogin.common.api.SpawnLocationHolder;

import java.util.UUID;

/**
 * Implementation of xLogin hook which contains unsafe statements.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class XLoginHookImpl implements Hook {
    private Location spawnLocation = null;
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "xLogin_Spigot");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        wrapper.getPlugin().getLogger().info("Hooked xLogin using " + PreferencesHolder.getConsumer().getClass().getName() + "!"); //Ensures that the class is loaded
        hooked = true;
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }

    public boolean isAuthenticated(UUID uuid) {
        return PreferencesHolder.getConsumer().getRegistry().isAuthenticated(uuid);
    }

    public Location getSpawnLocation() {
        if (spawnLocation == null) {
            spawnLocation = new Location(Bukkit.getWorld(SpawnLocationHolder.getWorldName()),
                    SpawnLocationHolder.getX(),
                    SpawnLocationHolder.getY(),
                    SpawnLocationHolder.getZ(),
                    SpawnLocationHolder.getPitch(),
                    SpawnLocationHolder.getYaw());
        }

        return spawnLocation;
    }

    public void resetSpawnLocation() {
        this.spawnLocation = null;
    }
}
