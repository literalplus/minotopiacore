/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.apache.commons.lang.Validate;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bukkit.entity.Player;

import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a clan in xClan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
class XClan implements Clan, MongoStoreable {
    private final XClanManager manager;
    private final int id;
    private XClanMemberSet memberSet;
    private XClanInvitationSet invitations;
    private XClanOptions options;
    private XClanBaseStorage baseStorage;
    private boolean valid = true;
    private String name;
    private String prefix;
    private boolean dirty = false;

    public XClan(XClanManager manager, int id) {
        this.manager = manager;
        this.id = id;
    }

    @Override
    public void broadcast(String message, boolean prependPrefix) {
        //FIXME
    }

    @Override
    public void announce(String message, boolean prependPrefix) {
        //FIXME
    }

    @Override
    public XClanInvitationSet getInvitations() {
        return invitations == null ? invitations = new XClanInvitationSet(this) : invitations;
    }

    @Override
    public Set<XClanMember> getAllMembers() {
        return getMemberSet().getMembers();
    }

    @Override
    public Set<UUID> getMemberIds() {
        return memberSet.getMembers().stream().map(XClanMember::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public Set<Player> getOnlineMembers() {
        Set<UUID> memberIds = memberSet.getMembers().stream().map(XClanMember::getUniqueId).collect(Collectors.toSet());
        return manager.getModule().getPlugin().getServer().getOnlinePlayers().stream()
                .filter(p -> memberIds.remove(p.getUniqueId()))
                .collect(Collectors.toSet());
    }

    @Override
    public void remove() {
        valid = false;
        throw new UnsupportedOperationException("not implemented"); //FIXME
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public XClanBaseStorage getBases() {
        return baseStorage == null ? baseStorage = new XClanBaseStorage(this) : baseStorage;
    }

    public XClanMemberSet getMemberSet() {
        return memberSet == null ? memberSet = new XClanMemberSet(this) : memberSet;
    }

    @Override
    public double getCofferBalance() {
        return getOptions().getCofferBalance();
    }

    @Override
    public int getLevel() {
        return getOptions().getLevel();
    }

    @Override
    public int getKills() {
        return getOptions().getKills();
    }

    @Override
    public int getDeaths() {
        return getOptions().getDeaths();
    }

    public XClanManager getManager() {
        return manager;
    }

    @Override
    public XClanOptions getOptions() {
        if (options == null) {
            options = new XClanOptions(this);
        }
        return options;
    }

    @Override
    public BsonDocument asMongo() {
        BsonDocument result = new BsonDocument()
                .append("_id", new BsonInt32(id))
                .append("name", new BsonString(name))
                .append("prefix", new BsonString(prefix));

        if (options != null && !options.asMap().isEmpty()) {
            result.append("options",  options.asMongo());
        }
        if (baseStorage != null && !baseStorage.getBases().isEmpty()) {
            result.append("bases", baseStorage.asMongo());
        }
        if (invitations != null && !invitations.getInvitations().isEmpty()) {
            result.append("invitations", invitations.asMongo());
        }
        if (memberSet != null && !memberSet.getMembers().isEmpty()) {
            result.append("members", memberSet.asMongo());
        }

        setDirty(false);

        return result;
    }

    @Override
    public void fromMongo(BsonValue dbValue) {
        BsonDocument dbObject = dbValue.asDocument();
        Validate.isTrue(containsAll(dbObject, "name", "prefix"), "Clans must have at least name and prefix!");
        this.name = dbObject.get("name").toString();
        this.prefix = dbObject.get("prefix").toString();

        if(dbObject.containsKey("options")) {
            getOptions().fromMongo(dbObject.get("options").asDocument());
        }
        if(dbObject.containsKey("bases")) {
            getBases().fromMongo(dbObject.get("bases").asDocument());
        }
        if(dbObject.containsKey("invitations")) {
            getInvitations().fromMongo(dbObject.get("invitations").asDocument());
        }
        if(dbObject.containsKey("members")) {
            getOptions().fromMongo(dbObject.get("members").asDocument());
        }
        setDirty(false);
    }

    private boolean containsAll(BsonDocument dbObject, String... keys) {
        return Arrays.stream(keys).allMatch(dbObject::containsKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XClan)) return false;

        XClan xClan = (XClan) o;

        return id == xClan.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

/*
{
        "_id" : NumberLong(1),
        "name" : "MinoTopiaTeam",
        "prefix" : "§3§lMTT",
        "leader_id" : "05793355-8827-4956-a878-cd314c4c3dc7",
        "level" : NumberLong(5),
        "kills" : NumberLong(1337),
        "deaths" : NumberLong(2),
        "bank" : NumberLong(12),
        "bases" : [
        {
        "_id" : NumberLong(1),
        "x" : NumberLong(0),
        "y" : NumberLong(70),
        "z" : NumberLong(0),
        "world" : "world",
        "pitch" : NumberLong(0),
        "yaw" : NumberLong(0)
        },
        {
        "_id" : NumberLong(2),
        "x" : NumberLong(1),
        "y" : NumberLong(70),
        "z" : NumberLong(1),
        "world" : "world_nether",
        "pitch" : NumberLong(0),
        "yaw" : NumberLong(0)
        }
        ],
        "options" : [],
        "members" : [
        {
        "uuid" : "05793355-8827-4956-a878-cd314c4c3dc7",
        "name" : "chris301234",
        "rank" : NumberLong(3),
        "permissions" : NumberLong(2097151)
        },
        {
        "uuid" : "a9503380-ff10-4e71-b717-ff05d129da13",
        "name" : "Literallie",
        "rank" : 2,
        "permissions" : 8191
        },
        {
        "uuid" : "9b7db083-b357-4f67-aae6-6593454986c8",
        "name" : "BranicYeti",
        "rank" : 0
        }
        ]
        }

 */
