/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import io.github.xxyy.mtc.module.clan.ClanModule;
import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.ClanManager;
import io.github.xxyy.mtc.module.clan.api.exception.NoSuchClanException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages a set of clans and provides methods to fetch and update clans and related data.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
public class XClanManager implements ClanManager {
    public static final String DATABASE_NAME = "mtc";
    public static final String CLAN_COLLECTION_NAME = "clans";
    private final ClanModule module;
    private final Map<Integer, XClan> clanCache = new HashMap<>();
    private final DBCollection mongoCol;

    public XClanManager(ClanModule module) {
        this.module = module;
        this.mongoCol = module.getMongo().getDB(DATABASE_NAME).getCollection(CLAN_COLLECTION_NAME);
    }

    public ClanModule getModule() {
        return module;
    }

    public Clan getClan(int id) {
        return clanCache.computeIfAbsent(id, this::fetchClan);
    }

    @Override
    public Clan getClanChecked(int id) throws NoSuchClanException {
        Clan clan = getClan(id);
        if (clan == null) {
            throw new NoSuchClanException("id: " + id);
        }
        return clan;
    }

    @Override
    public Clan getClanFor(UUID playerId) {
        return null; //FIXME
    }

    protected XClan fetchClan(int id) {
        XClan clan = clanCache.computeIfAbsent(id, id1 -> new XClan(this, id1));

        try(DBCursor cursor = mongoCol.find(new BasicDBObject("_id", id))) {
            clan.fromMongo(cursor.one());
        }

        return clan;
    }

    protected void saveClan(Clan clan) {

    }
}
