package io.github.xxyy.mtc.hook.pex;

import io.github.xxyy.lib.guava17.collect.ImmutableList;
import io.github.xxyy.mtc.hook.Hooks;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Simple implementation of a plugin hook for the Permissionsex plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-04
 */
public class PexHookImpl implements PexHook {
    @Nullable
    private PermissionManager permissionManager;

    @Override
    public Collection<PexGroup> getGroups() {
        if (permissionManager == null) {
            return ImmutableList.of();
        }

        return permissionManager.getGroupList().stream()
                .map(PexGroupWrapper::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailable(Plugin plugin) {
        return plugin.getServer().getPluginManager().getPlugin("PermissionsEx") != null;
    }

    @Override
    public void hook(Plugin plugin) throws Exception, NoClassDefFoundError {
        permissionManager = Hooks.setupProvider(PermissionManager.class, plugin);
    }
}
