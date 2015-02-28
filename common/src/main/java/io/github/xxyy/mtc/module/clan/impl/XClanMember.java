/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.impl;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import io.github.xxyy.lib.intellij_annotations.Nullable;
import io.github.xxyy.mtc.module.clan.api.Clan;
import io.github.xxyy.mtc.module.clan.api.ClanMember;
import io.github.xxyy.mtc.module.clan.api.ClanPermission;
import io.github.xxyy.mtc.module.clan.api.ClanRank;
import io.github.xxyy.mtc.module.clan.api.MongoStoreable;

import java.util.UUID;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/02/15
 */
public class XClanMember implements ClanMember, MongoStoreable {
    private static final BsonInt32 MEMBER_RANK_ID_BSON_INT = new BsonInt32(ClanRank.MEMBER.ordinal());
    private static final BsonInt32 MEMBER_PERM_MASK_BSON_INT = new BsonInt32(ClanRank.MEMBER.getDefaultPermissionMask());
    private static final String PERMISSION_MASK_KEY = "perms";
    private static final String RANK_ID_KEY = "rank";
    private static final String UUID_STRING_KEY = "uuid";
    public static final String LAST_NAME_KEY = "name";
    private final XClan clan;
    private final UUID uniqueId;
    @Nullable
    private String lastName;
    private ClanRank rank = ClanRank.MEMBER;
    private int permissionMask = rank.getDefaultPermissionMask();
    private boolean dirty;

    public XClanMember(XClan clan, UUID uniqueId) {
        this(clan, uniqueId, null);
    }

    public XClanMember(XClan clan, UUID uniqueId, String lastName) {
        this.clan = clan;
        this.uniqueId = uniqueId;
        this.lastName = lastName;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public int getPermissionMask() {
        return permissionMask;
    }

    @Override
    public ClanRank getRank() {
        return rank;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    @Override
    public Clan getClan() {
        return clan;
    }

    public void setRank(ClanRank rank) {
        setDirty(dirty || rank != this.rank);
        this.rank = rank;
    }

    public void setPermissionMask(int permissionMask) {
        setDirty(dirty || permissionMask != this.permissionMask);
        this.permissionMask = permissionMask;
    }

    public void setPermission(ClanPermission permission, boolean value) {
        if (this.hasPermission(permission) == value) {
            return; //No need to change anything
        }

        setPermissionMask(permissionMask ^ permission.bitValue()); //xor - changes the value no matter what it is currently, so if
        //it is not what we want it to be yet we can just xor it
    }

    @Override
    public BsonValue asMongo() {
        setDirty(false);
        return new BsonDocument(UUID_STRING_KEY, new BsonString(uniqueId.toString()))
                .append(RANK_ID_KEY, new BsonInt32(rank.ordinal()))
                .append(PERMISSION_MASK_KEY, new BsonInt32(permissionMask))
                .append(LAST_NAME_KEY, new BsonString(lastName));
    }

    @Override
    public void fromMongo(BsonValue obj) {
        BsonDocument doc = obj.asDocument();
        rank = ClanRank.values()[doc.getInt32(RANK_ID_KEY, MEMBER_RANK_ID_BSON_INT).intValue()];

        if (doc.containsKey(PERMISSION_MASK_KEY)) {
            permissionMask = doc.getInt32(PERMISSION_MASK_KEY, MEMBER_PERM_MASK_BSON_INT).intValue();
        }

        lastName = String.valueOf(doc.getString(LAST_NAME_KEY, lastName == null ? null : new BsonString(lastName)));
        setDirty(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XClanMember)) return false;

        XClanMember that = (XClanMember) o;
        return clan.equals(that.clan) && uniqueId.equals(that.uniqueId);

    }

    @Override
    public int hashCode() {
        int result = clan.hashCode();
        result = 31 * result + uniqueId.hashCode();
        return result;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public boolean setDirty(boolean dirty) {
        this.dirty = dirty;
        clan.setDirty(clan.isDirty() || dirty);
        return dirty;
    }
}
