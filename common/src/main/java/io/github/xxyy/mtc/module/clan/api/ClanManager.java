/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import io.github.xxyy.mtc.misc.CacheHelper;
import io.github.xxyy.mtc.module.clan.api.exception.NoSuchClanException;

import java.util.UUID;

/**
 * Manages clan objects with a cache and provides methods to retrieve clan information from the database.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public interface ClanManager extends CacheHelper.Cache {
    /**
     * Returns a clan reference by its unique id. The data and reference may be cached.
     *
     * @param id the unique integer identifier of the clan to retrieve
     * @return a clan reference with the passed id or null if no such clan exists
     */
    public Clan getClan(int id);

    /**
     * Tries to retrieve a clan reference by its id, returning either a freshly-constructed object or one retrieved from
     * the cache.
     *
     * @param id the id to match
     * @return a clan reference with that id
     * @throws NoSuchClanException if no such clan exists
     */
    public Clan getClanChecked(int id) throws NoSuchClanException;


    /**
     * Tries to retrieve a player's clan.
     *
     * @param playerId the unique id of the player to find
     * @return a clan reference or null if the player is not member of any clan
     */
    public Clan getClanFor(UUID playerId);
}
