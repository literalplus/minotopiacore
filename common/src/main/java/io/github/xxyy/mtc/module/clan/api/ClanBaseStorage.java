/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import org.bukkit.Location;

import java.util.Map;

/**
 * Manages bases of a clan. Bases are user-defined, changeable locations which clan members can teleport to at any time.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public interface ClanBaseStorage {
    /**
     * Gets a location considered the main base. This can for example be determined by a special name. If there is any
     * base, it will be returned. The only way this could return null is if there were no bases at all.
     *
     * @return a location considered the main base or null if there are no bases
     */
    Location getMainBase();

    /**
     * Sets a location to be considered the main base. If any base has been set as main base before, it will be
     * overridden. If no main base has been specified explicitly, but other bases are present, those are left alone.
     *
     * @param location the  location of the new main base
     */
    void setMainBase(Location location);

    /**
     * Gets a base, if set.
     *
     * @param key the key to seek
     * @return the location of the requested base or null if there is no such base
     */
    Location getBase(String key);

    /**
     * Checks whether there is a base associated with a certain key.
     *
     * @param key the key to seek
     * @return whether a base is associated with that key
     */
    boolean hasBase(String key);

    /**
     * @return a map of all bases saved in the storage
     */
    Map<String, Location> getBases();

    /**
     * Associates a base location with a key.
     *
     * @param key      the key to associate that base with
     * @param location the base location to save
     */
    void setBase(String key, Location location);

    /**
     * Deletes a base.
     *
     * @param key the key to remove
     * @return whether a base by that key has been removed.
     */
    boolean deleteBase(String key);

    /**
     * @return the clan this storage belongs to
     */
    Clan getClan();
}
