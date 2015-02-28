/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import java.util.function.Predicate;

/**
 * Stores arbitrary objects in the underlying database in relation to a clan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public interface ClanOptions {

    /**
     * @return the clan these options apply to
     */
    Clan getClan();

    /**
     * Fetches an option from the cached options.
     * @param key the key to seek
     * @return the associated value or null if no value has been associated.
     */
    BsonValue get(String key);

    /**
     * Fetches an option from the cache's options, returning a default value if no value has been associated with that key.
     * @param key the key to seek
     * @param def the default value
     * @return the value of the option or the default value if unset
     */
    BsonValue getOrDefault(String key, BsonValue def);

    /**
     * @return a representation of these options as BSON document
     */
    BsonDocument asBsonDocument();

    /**
     * Checks if any value has been associated with a key.
     * @param key the key to seek
     * @return whether there is a value associated with that key
     */
    boolean isSet(String key);

    /**
     * Saves an object to the options cache. It will be written back to database eventually.
     * @param key the key to associated with
     * @param value the value to associate
     */
    void put(String key, BsonValue value);

    /**
     * Gets an integer or 0 if the value has not been set or is not an integer.
     * @param key the key to seek
     * @return the associated integer or 0 otherwise
     */
    int getInt(String key);

    /**
     * Gets a value if it is of a specific class
     * @param key the key to seek
     * @param clazz the required class
     * @param def the default value
     * @param <T> the return type
     * @return the value of the requested type or the default value otherwise
     */
    <T extends BsonValue> T getIfIs(String key, Class<T> clazz, T def);

    /**
     * Saves a value to the options storage if a function applies.
     * @param <T> the type of the value
     * @param key the key to seek
     * @param value the value to associate
     * @param predicate the function which checks whether to actually put
     */
    <T extends BsonValue> void putIf(String key, T value, Predicate<T> predicate);
}
