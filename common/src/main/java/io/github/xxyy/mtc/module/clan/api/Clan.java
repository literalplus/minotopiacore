/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a clan (a unique bond between multiple players who play together, fight together, etc).
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04/02/15
 */
public interface Clan {
    /**
     * Sends a message to all online members of this clan. Optionally prepends a plugin-specific prefix.
     *
     * @param message       the message to broadcast
     * @param prependPrefix whether to prepend a plugin-specific prefix
     */
    void broadcast(String message, boolean prependPrefix);

    /**
     * Announces a message to all online clan members and additionally saves it for offline members, which will
     * receive the announcement the next time they join the server.
     *
     * @param message       the message to announce
     * @param prependPrefix whether to prepend a plugin-specific prefix
     */
    void announce(String message, boolean prependPrefix);

    /**
     * @return the invitation set of this clan
     */
    ClanInvitationSet getInvitations();

    /**
     * @return a set of the unique ids of all members of this clan
     */
    Set<UUID> getMemberIds();

    /**
     * @return a set of all players of this clan which are currently online
     */
    Set<Player> getOnlineMembers();

    /**
     * Permanently and irrevocably removes this clan and all assiociated data.
     */
    void remove();

    /**
     * @return whether this clan still exists in the database
     */
    boolean isValid();

    /**
     * @return the unique integer id uniquely identifying this clan in the database
     */
    int getId();

    /**
     * @return the user-defined, changeable and human-readable name describing this clan
     */
    String getName();

    /**
     * @return the short chat prefix of this clan
     */
    String getPrefix();

    /**
     * @return the unique id of the leader of this clan
     */
    UUID getLeaderId();

    /**
     * @return the user-defined and changeable location of the base of this clan
     */
    Location getBaseLocation();

    /**
     * retrieves the amount of money in this clan's coffer. (A supply or store of money, often belonging to an organization)
     *
     * @return the amount of money in this clan's coffer
     */
    int getCoffer();

    /**
     * @return the level of this clan, starting at 0.
     * @see io.github.xxyy.mtc.module.clan.api.ClanLevels
     */
    int getLevel();

    /**
     * @return the amount of total kills this clan's members have accumulated while in the clan
     */
    int getKills();

    /**
     * @return the amount of total deaths this clan's members have accumulated while in teh clan
     */
    int getDeaths();

    /**
     * Computes the overall kill/death ratio of this clan. For simplicity purposes, no deaths are the same as one death.
     *
     * @return the amount of kills divided by the amount of deaths
     */
    default double getKillDeathRatio() {
        return getKills() / (getDeaths() == 0 ? 1 : getDeaths());
    }

    /**
     * @return the colored prefix for this clan, where the color depends on the level
     */
    default String getDisplayPrefix() {
        return ClanLevels.getPrefixColor(getLevel()) + getPrefix();
    }
}
