/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.xxyy.mtc.hook.impl.XLoginHookImpl;

import java.util.List;
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
        return isActive() && unsafe.isAuthenticated(uuid);
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

    public String getDisplayString(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if(onlinePlayer != null) {
            return onlinePlayer.getName();
        }

        String foundName = unsafe.getName(uuid);
        if(foundName != null) {
            return foundName;
        }

        return uuid.toString();
    }

    public List<Profile> getProfiles(String nameOrId) {
        return unsafe.getProfiles(nameOrId);
    }

    public Profile getBestProfile(String nameOrId) {
        List<Profile> profiles = unsafe.getProfiles(nameOrId);

        if(profiles.size() == 1) {
            return profiles.get(0); //If there's only one premium player, xLogin only returns that one
        } else {
            return null;
        }
    }

    public UUID getBestUniqueId(String nameOrId) {
        Profile profile = getBestProfile(nameOrId);
        return profile == null ? null : profile.getUniqueId();
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }

    public interface Profile {
        boolean isPremium();
        String getName();
        UUID getUniqueId();
        String getLastIp();
    }
}
