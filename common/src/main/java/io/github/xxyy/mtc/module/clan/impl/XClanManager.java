/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.client.MongoCollection;
import org.apache.commons.lang.Validate;
import org.bson.BsonDocument;
import org.bson.Document;

import io.github.xxyy.mtc.module.clan.ClanModule;
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
    private final MongoCollection<BsonDocument> mongoCol;

    public XClanManager(ClanModule module) {
        this.module = module;
        this.mongoCol = module.getMongo()
                .getDatabase(DATABASE_NAME)
                .getCollection(CLAN_COLLECTION_NAME)
                .withDefaultClass(BsonDocument.class);
    }

    public ClanModule getModule() {
        return module;
    }

    public XClan getClan(int id) {
        return clanCache.computeIfAbsent(id, this::fetchOrCreateClan);
    }

    @Override
    public XClan getClanChecked(int id) throws NoSuchClanException {
        XClan clan = getClan(id);
        if (clan == null) {
            throw new NoSuchClanException("id: " + id);
        }
        return clan;
    }

    @Override
    public XClan getClanFor(UUID playerId) {
        return null; //FIXME
    }

    protected XClan fetchOrCreateClan(int id) {
        return fetchClan(id);
    }

    protected XClan fetchClan(int id) {
        XClan clan = clanCache.get(id);
        BsonDocument doc = mongoCol.find(new Document("_id", id)).first();

        if(doc != null) {
            if (clan == null) {
                clan = new XClan(this, id);
            }
            clan.fromMongo(doc);
        } else { //Clan does (no longer) exist in database
            clanCache.remove(id);
            return null;
        }

        return clan;
    }

    protected void saveClan(XClan clan) {
        Validate.notNull(clan, "Cannot save null clan!");
        mongoCol.replaceOne(new Document("_id", clan.getId()), clan.asMongo());
    }
}
