/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import io.github.xxyy.mtc.module.clan.ClanModule;

/**
 * Lists possible clan levels.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04/02/15
 */
public final class ClanLevels {
    private static char[] levelColors = "baed9786521".toCharArray();

    /**
     * Calculates the cost of upgrading a clan to the next level
     *
     * @param clan the target clan
     * @return the cost of upgrading specified clan to the next level
     */
    public static int getCost(Clan clan) {
        return (int) Math.floor(2_000 * Math.pow(1.42d, clan.getLevel() + 1));
    }

    /**
     * Computes a print-ready chat color string for a clan level.
     *
     * @param level the target level
     * @return a formatted string representing the color of the clan prefix, as accepted by Minecraft
     */
    public static String getPrefixColor(int level) {
        if (level > levelColors.length) {
            return "§o§" + levelColors[levelColors.length - 1];
        } else {
            return "§" + levelColors[level];
        }
    }

    /**
     * Computes the clan member limit for a specific clan level.
     *
     * @param level the target level
     * @return the member limit
     */
    public static int getMemberLimit(int level) {
        return ClanModule.BASE_CLAN_MEMBER_LIMIT + level;
    }
}
