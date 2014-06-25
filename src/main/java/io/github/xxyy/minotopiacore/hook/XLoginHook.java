package io.github.xxyy.minotopiacore.hook;

import io.github.xxyy.minotopiacore.hook.impl.XLoginHookImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public boolean isAuthenticated(Player plr) {
        return isAuthenticated(plr.getUniqueId());
    }

    public boolean isAuthenticated(UUID uuid) {
        return isActive() && Hooks.Unsafe.safeCall(unsafe::isAuthenticated, uuid, true, null);
    }

    public Location getSpawnLocation() {
        if(!isActive()) {
            return null;
        }

        return Hooks.Unsafe.safeCall(unsafe::getSpawnLocation, null, null);
    }

    public void resetSpawnLocation() {
        if(isActive()) {
            Hooks.Unsafe.safeCall(unsafe::resetSpawnLocation, null);
        }
    }

    @Override
    public boolean isActive() {
        return unsafe != null;
    }
}
