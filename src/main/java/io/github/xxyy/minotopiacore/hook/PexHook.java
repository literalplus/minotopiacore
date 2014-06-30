package io.github.xxyy.minotopiacore.hook;

import io.github.xxyy.minotopiacore.hook.impl.PexHookImpl;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helps interfacing with PermissionsEx.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class PexHook extends SimpleHookWrapper {
    private PexHookImpl unsafe;

    public PexHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public List<PexHook.Group> getGroupList() {
        if(!isActive()) {
            this.getPlugin().getLogger().info("Could not find Pemissionsex groups because not active!"); //hehe
            return new ArrayList<>();
        }

        //noinspection Convert2Diamond
        return Hooks.Unsafe.safeCall(unsafe::getGroupList, new ArrayList<PexHook.Group>(), null); //diamond inference causes IDEA to track a compile error
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isActive();
    }

    public static interface User {
        String getIdentifier();
        UUID getUniqueId();
        String getName();
    }

    public static interface Group {
        String getName();
        String getPrefix();
        List<User> getUsers();
        boolean getOptionBoolean(String name, String world, boolean def);
        int getOptionInteger(String name, String world, int def);
    }
}
