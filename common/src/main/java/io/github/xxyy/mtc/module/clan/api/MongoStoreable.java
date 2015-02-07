/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import com.mongodb.DBObject;

/**
 * An object which can be serialize itself into a MongoDB-compatible format.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public interface MongoStoreable {
    /**
     * @return a view of this object in the MongoDB object format
     */
    DBObject asMongo();

    /**
     * Popularises this object with the values from a passed MongoDB object.
     *
     * @param dbObject the object to get values from
     */
    void fromMongo(DBObject dbObject);
}