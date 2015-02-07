/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.github.xxyy.mtc.module.clan.api.ClanMember;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores member information for a clan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
class XClanMemberSet implements MongoStoreable {
    private Set<ClanMember> members = new HashSet<>();

    @Override
    public BasicDBObject asMongo() {
        return null;
    }

    @Override
    public void fromMongo(DBObject dbObject) {

    }

    //FIXME
}
