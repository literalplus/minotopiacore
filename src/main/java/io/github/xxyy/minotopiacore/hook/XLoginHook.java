package io.github.xxyy.minotopiacore.hook;

import io.github.xxyy.minotopiacore.hook.impl.XLoginHookImpl;
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
        return isActive() && unsafe.isAuthenticated(uuid);
    }

    @Override
    public boolean isActive() {
        return unsafe != null;
    }
}
