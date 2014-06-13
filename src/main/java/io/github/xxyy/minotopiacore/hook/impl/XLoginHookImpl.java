package io.github.xxyy.minotopiacore.hook.impl;

import io.github.xxyy.minotopiacore.hook.XLoginHook;
import io.github.xxyy.xlogin.common.PreferencesHolder;
import io.github.xxyy.xlogin.common.api.SpawnLocationHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Implementation of xLogin hook which contains unsafe statements.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class XLoginHookImpl {
    private Location spawnLocation = null;

    public XLoginHookImpl(XLoginHook wrapper) {

    }

    public boolean isAuthenticated(UUID uuid) {
        return PreferencesHolder.getConsumer().getRegistry().isAuthenticated(uuid);
    }

    public Location getSpawnLocation() {
        if(spawnLocation == null) {
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
