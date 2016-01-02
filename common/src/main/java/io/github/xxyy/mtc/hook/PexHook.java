/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook;

import com.google.common.collect.ImmutableList;
import io.github.xxyy.mtc.hook.impl.PexHookImpl;
import org.bukkit.plugin.Plugin;

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
            this.getPlugin().getLogger().info("Could not find PermissionsEx groups because not active!");
            return ImmutableList.of();
        }

        return unsafe.getGroupList();
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }

    public interface User {
        String getIdentifier();
        boolean hasUniqueId();
        UUID getUniqueId();
        String getName();
    }

    public interface Group {
        String getName();
        String getPrefix();
        List<User> getUsers();
        boolean getOptionBoolean(String name, String world, boolean def);
        int getOptionInteger(String name, String world, int def);
    }
}
