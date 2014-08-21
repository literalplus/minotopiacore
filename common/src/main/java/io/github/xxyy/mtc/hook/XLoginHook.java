package io.github.xxyy.mtc.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.xxyy.mtc.hook.impl.XLoginHookImpl;

import java.util.UUID;

/**
 * Helps interfacing with the xLogin plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class XLoginHook extends SimpleHookWrapper {
    private XLoginHookImpl unsafe;

    public XLoginHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public boolean isAuthenticated(Player plr) throws IllegalStateException {
        return isAuthenticated(plr.getUniqueId());
    }

    public boolean isAuthenticated(UUID uuid) throws IllegalStateException {
        return isActive() && isAuthenticated(uuid);
    }

    public Location getSpawnLocation() {
        if(!isActive()) {
            return null;
        }

        return unsafe.getSpawnLocation();
    }

    public void resetSpawnLocation() {
        if(isActive()) {
            unsafe.resetSpawnLocation();
        }
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
