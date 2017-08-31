/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.hook.impl;

import com.google.common.collect.ImmutableList;
import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;
import li.l1t.mtc.hook.PexHook;
import org.apache.commons.lang.Validate;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import javax.annotation.Nullable;
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
        if (permissionManager == null) {
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
        public boolean hasUniqueId() {
            return uniqueId != null;
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
