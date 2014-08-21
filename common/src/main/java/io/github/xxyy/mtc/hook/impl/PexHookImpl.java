package io.github.xxyy.mtc.hook.impl;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.mtc.hook.HookWrapper;
import io.github.xxyy.mtc.hook.Hooks;
import io.github.xxyy.mtc.hook.PexHook;
import org.apache.commons.lang.Validate;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import io.github.xxyy.lib.intellij_annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements unsafe parts of the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class PexHookImpl implements Hook {
    @Nullable
    private PermissionManager permissionManager;
    private PexHook wrapper;

    @Override
    public boolean isHooked() {
        return wrapper != null && permissionManager != null;
    }

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return wrapper instanceof PexHook && Hooks.isPluginLoaded(wrapper, "PermissionsEx");
    }

    @Override
    @SuppressWarnings("ConstantConditions") //We're checking anyways lol
    public void hook(HookWrapper wrapper) {
        Validate.isTrue(wrapper instanceof PexHook, "incompatible hook wrapper!");
        this.wrapper = (PexHook) wrapper;
        this.permissionManager = Hooks.setupProvider(PermissionManager.class, wrapper.getPlugin());
    }

    public List<PexHook.Group> getGroupList() {
        if(permissionManager == null) {
            return ImmutableList.of();
        }

        return permissionManager.getGroupList().stream()
                .map(GroupImpl::new)
                .collect(Collectors.toList());
    }

    public class UserImpl implements PexHook.User {
        private final PermissionUser handle;
        private UUID uniqueId; //Can't be final because Java thinks that UUID#fromString() can throw an IAE after the value has been assigned ._.

        public UserImpl(PermissionUser user) {
            this.handle = user;

            try {
                this.uniqueId = UUID.fromString(user.getIdentifier());
            } catch (IllegalArgumentException e) {
                wrapper.getPlugin().getLogger().warning("Invalid UUID for this user: " + getIdentifier());
                this.uniqueId = null;
            }
        }

        public PermissionUser getHandle() {
            return handle;
        }

        @Override
        public String getName() {
            return handle.getName();
        }

        @Override
        public String getIdentifier() {
            return handle.getIdentifier();
        }

        @Override
        public UUID getUniqueId() {
            Validate.notNull(uniqueId, "Invalid UUID for this permission user: " + getIdentifier());

            return uniqueId;
        }

        public boolean isValid() {
            return getUniqueId() != null;
        }
    }

    public class GroupImpl implements PexHook.Group {
        private final PermissionGroup handle;

        public GroupImpl(PermissionGroup group) {
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
        public List<PexHook.User> getUsers() {
            //noinspection Convert2MethodRef
            return handle.getUsers().stream()
                    .map(puser -> new UserImpl(puser)) //constructor ref is broken in j8u5 for some reason
                    .filter(UserImpl::isValid)
                    .collect(Collectors.toList());
        }
    }
}
