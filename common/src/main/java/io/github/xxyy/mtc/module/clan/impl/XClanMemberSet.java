/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.bson.BsonArray;
import org.bson.BsonValue;

import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Stores member information for a clan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
class XClanMemberSet implements MongoStoreable {
    private final XClan clan;

    private Set<XClanMember> members = new HashSet<>();

    XClanMemberSet(XClan clan) {
        this.clan = clan;
    }

    @Override
    public BsonValue asMongo() {
        return new BsonArray(members.stream()
                .map(XClanMember::asMongo)
                .collect(Collectors.toList()));
    }

    @Override
    public void fromMongo(BsonValue val) {
        members.clear();

        //noinspection ConstantConditions
        val.asArray().stream()
                .map(BsonValue::asDocument)
                .filter(obj -> obj.containsKey("uuid"))
                .forEach(obj -> members.add(new XClanMember(clan, UUID.fromString(obj.get("uuid").toString()))));
    }

    @Override
    public boolean isDirty() {
        return members.stream().anyMatch(MongoStoreable::isDirty);
    }

    public XClan getClan() {
        return clan;
    }

    public Set<XClanMember> getMembers() {
        return members;
    }

    public boolean addMember(UUID uuid, String lastName) { //TODO announcement
        if (members.stream().anyMatch(xcm -> xcm.getUniqueId().equals(uuid))) {
            return false;
        }

        members.add(new XClanMember(clan, uuid, lastName));
        return true;
    }

    public boolean removeMember(UUID uuid) { //TODO announcement
        return members.removeIf(xcm -> xcm.getUniqueId().equals(uuid));
    }
}
