/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.pvpstats.model;

import io.github.xxyy.mtc.hook.XLoginHook;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Stores the current PvP stats of a player.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-03
 */
public class PlayerStats {
    private final UUID uniqueId;
    private String name;
    private int kills;
    private int deaths;

    /**
     * Constructs a new player stats object.
     *
     * @param uniqueId the unique id of the player
     * @param name     the last known name of the player or null if unknown
     * @param kills    the amount of kills the player currently has
     * @param deaths   the amount of deaths the player currently has
     */
    public PlayerStats(UUID uniqueId, @Nullable String name, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * Gets a display name for the player associated with this stats object, possibly querying the xLogin database for
     * the name. If the name is unknown to the database, the unique id is returned.
     *
     * @return a display name for the player associated with this stats object
     */
    public String getDisplayName(XLoginHook xLoginHook) {
        if (name == null) {
            name = xLoginHook.getDisplayString(uniqueId);
        }
        return name;
    }

    /**
     * Adds an integer value to the amount of kills the associated player has currently.
     *
     * @param modifier the modifier
     * @return the new amount of kills
     */
    public int addKills(int modifier) {
        return kills = kills + modifier;
    }

    /**
     * Adds an integer value to the amount of deaths the associated player has currently.
     *
     * @param modifier the modifier
     * @return the new amount of deaths
     */
    public int addDeaths(int modifier) {
        return deaths = deaths + modifier;
    }

    /**
     * @return the unique id of the player associated with this stats object
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * @return the amount of kills the associated player has currently
     */
    public int getKills() {
        return kills;
    }

    /**
     * Sets the amount of kills the associated player has currently.
     *
     * @param kills the new amount of kills
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * @return the amount of deaths the associated player has currently
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Sets the amount of deaths the associated player has currently.
     *
     * @param deaths the new amount of deaths
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerStats)) return false;

        PlayerStats that = (PlayerStats) o;

        if (kills != that.kills) return false;
        if (deaths != that.deaths) return false;
        if (!uniqueId.equals(that.uniqueId)) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = uniqueId.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + kills;
        result = 31 * result + deaths;
        return result;
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "uniqueId=" + uniqueId +
                ", name='" + name + '\'' +
                ", kills=" + kills +
                ", deaths=" + deaths +
                '}';
    }
}
