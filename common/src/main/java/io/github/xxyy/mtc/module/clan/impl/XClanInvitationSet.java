/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bukkit.entity.Player;

import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.ClanInvitationSet;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 06/02/15
 */
public class XClanInvitationSet implements ClanInvitationSet, MongoStoreable {
    private static final String TARGET_ID_PATH = "tid";
    private static final String SOURCE_ID_PATH = "sid";
    private final XClan clan;
    private final Map<UUID, UUID> invitations = new HashMap<>();
    private final Map<UUID, UUID> immutableInvitations = Collections.unmodifiableMap(invitations);
    private boolean dirty = false;

    public XClanInvitationSet(XClan clan) {
        this.clan = clan;
    }

    @Override
    public Clan getClan() {
        return clan;
    }

    @Override
    public Map<UUID, UUID> getInvitations() {
        return immutableInvitations;
    }

    @Override
    public UUID revoke(UUID targetId) {
        setDirty(true);
        return invitations.remove(targetId);
    }

    @Override
    public boolean accept(UUID targetId) {
        invitations.remove(targetId);
        //FIXME needs to actually add the player to the clan
        setDirty(true);
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void invite(UUID sourceId, UUID targetId) {
        setDirty(true);
        invitations.put(targetId, sourceId); //TODO: mark clan for saving or something
    }

    @Override
    public void announce(Player plr) {
        //FIXME
    }

    @Override
    public BsonValue asMongo() {
        setDirty(false);
        BsonArray result = new BsonArray();
        invitations.entrySet().stream()
                .forEach(e -> result.add(new BsonDocument()
                        .append(TARGET_ID_PATH, new BsonString(e.getKey().toString()))
                        .append(SOURCE_ID_PATH, new BsonString(e.getValue().toString()))));
        return result;
    }

    @Override
    public void fromMongo(BsonValue dbObject) {
        invitations.clear();
        //noinspection ConstantConditions
        dbObject.asArray().stream()
                .map(BsonValue::asDocument)
                .forEach(this::loadInvitationFromMongo);
        setDirty(false);
    }

    private void loadInvitationFromMongo(BsonDocument dbObject) {
        if (!dbObject.containsKey(TARGET_ID_PATH) || !dbObject.containsKey(SOURCE_ID_PATH)) {
            clan.getManager().getModule().getPlugin().getLogger().info("Omitting a clan invitation because it was invalid");
            return;
        }
        invitations.put(UUID.fromString(dbObject.getString(TARGET_ID_PATH).getValue()),
                UUID.fromString(dbObject.getString(SOURCE_ID_PATH).getValue()));
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public boolean setDirty(boolean dirty) {
        this.dirty = dirty;
        clan.setDirty(clan.isDirty() || dirty);
        return this.dirty;
    }
}
