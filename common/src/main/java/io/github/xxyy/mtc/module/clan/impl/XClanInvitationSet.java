/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang.Validate;
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
        return invitations.remove(targetId);
    }

    @Override
    public boolean accept(UUID targetId) {
        invitations.remove(targetId);
        //FIXME needs to actually add the player to the clan
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void invite(UUID sourceId, UUID targetId) {
        invitations.put(targetId, sourceId); //TODO: mark clan for saving or something
    }

    @Override
    public void announce(Player plr) {
        //FIXME
    }

    @Override
    public DBObject asMongo() {
        BasicDBList result = new BasicDBList();
        invitations.entrySet().stream()
                .forEach(e -> result.add(new BasicDBObject()
                        .append(TARGET_ID_PATH, e.getKey())
                        .append(SOURCE_ID_PATH, e.getValue())));
        return result;
    }

    @Override
    public void fromMongo(DBObject dbObject) {
        invitations.clear();
        Validate.isTrue(dbObject instanceof BasicDBList, "cannot retrieve invitations from anything else than a list!");
        //noinspection ConstantConditions
        ((BasicDBList) dbObject).stream()
                .filter(obj -> obj instanceof DBObject) //Discard everything else that doesn't belong there
                .forEach(obj -> loadInvitationFromMongo((DBObject) obj));
    }

    private void loadInvitationFromMongo(DBObject dbObject) {
        if (!dbObject.containsField(TARGET_ID_PATH) || !dbObject.containsField(SOURCE_ID_PATH)) {
            clan.getManager().getModule().getPlugin().getLogger().info("Omitting a clan invitation because it was invalid");
            return;
        }
        invitations.put(UUID.fromString(dbObject.get(TARGET_ID_PATH).toString()),
                UUID.fromString(dbObject.get(SOURCE_ID_PATH).toString()));
    }
}
