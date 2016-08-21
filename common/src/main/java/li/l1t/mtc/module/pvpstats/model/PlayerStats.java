/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.pvpstats.model;

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
    private boolean dirty = false;

    /**
     * Constructs a new player stats object.
     *
     * @param uniqueId the unique id of the player
     * @param name     the last known name of the player or null if unknown
     * @param kills    the amount of kills the player currently has
     * @param deaths   the amount of deaths the player currently has
     */
    public PlayerStats(UUID uniqueId, String name, int kills, int deaths) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * Gets a display name for the player associated with this stats object.
     *
     * @return a display name for the player associated with this stats object
     */
    public String getDisplayName() {
        return name;
    }

    /**
     * Adds an integer value to the amount of kills the associated player has currently.
     *
     * @param modifier the modifier
     * @return the new amount of kills
     */
    public int addKills(int modifier) {
        return setKills(kills + modifier);
    }

    /**
     * Adds an integer value to the amount of deaths the associated player has currently.
     *
     * @param modifier the modifier
     * @return the new amount of deaths
     */
    public int addDeaths(int modifier) {
        return setDeaths(deaths + modifier);
    }

    /**
     * Returns the kill/death ratio of the associated player. This returns {@link
     * Double#POSITIVE_INFINITY} if the player does not have any deaths yet. The mathematically more
     * useful kills/lives approach has been turned down by management, so ranking is not possible.
     * The user interface must not show the K/DR if it is equal to {@link
     * Double#POSITIVE_INFINITY}.
     *
     * @return the ratio kills/deaths or {@link Double#POSITIVE_INFINITY} if deaths = 0
     */
    public double getKDRatio() {
        return deaths == 0 ? Double.POSITIVE_INFINITY : ((double) kills / (double) deaths);
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
     * @return the new amount of kills
     */
    public int setKills(int kills) {
        if (this.kills != kills) {
            markDirty();
        }
        return this.kills = kills;
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
     * @return the new amount of deaths
     */
    public int setDeaths(int deaths) {
        if (this.deaths != deaths) {
            markDirty();
        }
        return this.deaths = deaths;
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
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = uniqueId.hashCode();
        result = 31 * result + name.hashCode();
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

    public boolean isDirty() {
        return dirty;
    }

    private void markDirty() {
        setDirty(true);
    }

    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
