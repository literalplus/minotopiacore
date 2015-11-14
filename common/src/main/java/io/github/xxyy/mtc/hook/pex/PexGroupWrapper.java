package io.github.xxyy.mtc.hook.pex;

import ru.tehkode.permissions.PermissionGroup;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementation of a wrapper for Permissionsex group objects.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-14
 */
public class PexGroupWrapper implements PexGroup {
    private final PermissionGroup handle;

    public PexGroupWrapper(PermissionGroup group) {
        this.handle = group;
    }

    public PermissionGroup getHandle() {
        return handle;
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public String getPrefix() {
        return handle.getPrefix();
    }

    @Override
    public int getOptionInteger(String optionName, String world, int defaultValue) {
        return handle.getOptionInteger(optionName, world, defaultValue);
    }

    @Override
    public boolean getOptionBoolean(String optionName, String world, boolean defaultValue) {
        return handle.getOptionBoolean(optionName, world, defaultValue);
    }

    @Override
    public Collection<PexUser> getUsers() {
        return handle.getUsers().stream()
                .map(PexUserWrapper::new)
                .collect(Collectors.toList());
    }
}
