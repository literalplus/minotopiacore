package io.github.xxyy.minotopiacore.hook.impl;

import io.github.xxyy.minotopiacore.hook.PexHook;
import org.apache.commons.lang.Validate;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements unsafe parts of the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class PexHookImpl {

    private final PexHook wrapper;

    public PexHookImpl(PexHook wrapper) {
        this.wrapper = wrapper;
    }

    public List<PexHook.Group> getGroupList() {
        return PermissionsEx.getPermissionManager().getGroupList().stream()
                .map(GroupImpl::new)
                .collect(Collectors.toList());
    }

    public boolean isActive() {
        return PermissionsEx.isAvailable();
    }

    public class UserImpl implements PexHook.User {
        private final PermissionUser parent;
        private UUID uniqueId; //Can't be final because Java thinks that UUID#fromString() can throw an IAE after the value has been assigned ._.

        public UserImpl(PermissionUser user) {
            this.parent = user;

            try {
                this.uniqueId = UUID.fromString(user.getIdentifier());
            } catch (IllegalArgumentException e) {
                wrapper.getPlugin().getLogger().warning("Invalid UUID for this user: " + getIdentifier());
                this.uniqueId = null;
            }
        }

        public PermissionUser getParent() {
            return parent;
        }

        @Override
        public String getName() {
            return parent.getName();
        }

        @Override
        public String getIdentifier() {
            return parent.getIdentifier();
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
        private final PermissionGroup parent;

        public GroupImpl(PermissionGroup group) {
            this.parent = group;
        }

        public PermissionGroup getParent() {
            return parent;
        }

        @Override
        public String getName() {
            return parent.getName();
        }

        @Override
        public String getPrefix() {
            return parent.getPrefix();
        }

        @Override
        public int getOptionInteger(String optionName, String world, int defaultValue) {
            return parent.getOptionInteger(optionName, world, defaultValue);
        }

        @Override
        public boolean getOptionBoolean(String optionName, String world, boolean defaultValue) {
            return parent.getOptionBoolean(optionName, world, defaultValue);
        }

        @Override
        public List<PexHook.User> getUsers() {
            return parent.getUsers().stream()
                    .map(UserImpl::new)
                    .filter(UserImpl::isValid)
                    .collect(Collectors.toList());
        }
    }
}
