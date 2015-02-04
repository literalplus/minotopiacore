/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Manages invitations for a clan.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04/02/15
 */
public interface ClanInvitationSet {
    /**
     * @return the clan this invitation belongs to
     */
    Clan getClan();

    /**
     * @return an immutable map from invitation target UUID to source UUID representing this set's invitations
     */
    Map<UUID, UUID> getInvitations();

    /**
     * @param targetId the target to look up
     * @return the unique id of the player who initiated the invitation for {@code targetId}.
     */
    default UUID getSourceId(UUID targetId) {
        return getInvitations().get(targetId);
    }

    /**
     * Checks whether a player has been invited to the associated clan by its UUID.
     *
     * @param targetId the unique id of the player to look up
     * @return whether an invitation has been issued for said player
     */
    default boolean hasInvitation(UUID targetId) {
        return getInvitations().containsKey(targetId);
    }

    /**
     * Revokes an invitation.
     *
     * @param targetId the unique id of the user whose invitation to revoke
     * @return the unique id of the issuer of the invitation or null if there is no such invitation
     */
    UUID revoke(UUID targetId);

    /**
     * Accepts an invitation. This removes the invitation and additionally adds the target to the clan.
     *
     * @param targetId the unique id of the target player
     * @return whether the target player is now member of the associated clan
     */
    boolean accept(UUID targetId);

    /**
     * Invites a player into the associated clan.
     *
     * @param sourceId the unique id of the player who initiated the invitation
     * @param targetId the unique id of the invitation target
     * @throws java.lang.IllegalArgumentException if the source is not in the associated clan or the target is already in another clan
     */
    void invite(UUID sourceId, UUID targetId);

    /**
     * @return the unique id of the player who has been offered to join the clan
     */
    UUID getTargetId();

    /**
     * @return the unique id of the player who has initiated the invitation
     */
    UUID getSourceId();

    /**
     * Announces to a player that they have been given this invitation in a nice manner, also featuring how to accept or
     * decline this offer.
     *
     * @param plr the player to send the message to
     * @throws java.lang.IllegalArgumentException if {@code plr} is not the recipient of this invitation
     */
    void announce(Player plr);


}
