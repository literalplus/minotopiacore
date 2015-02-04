/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import java.util.UUID;

/**
 * Represents a clan member.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
public interface ClanMember {
    /**
     * @return the unique id associated with this clan member
     */
    UUID getUniqueId();

    /**
     * @return an integer bitmask specifying this user's permissions
     */
    int getPermissionMask();

    /**
     * @return this member's internal clan rank
     */
    ClanRank getRank();

    /**
     * @return the associated clan
     */
    Clan getClan();

    /**
     * Checks whether this member has a clan permission.
     *
     * @param permission the permission to look up
     * @return whether this member has that permission
     */
    default boolean hasPermission(ClanPermission permission) {
        return permission.has(this);
    }
}
