/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

/**
 * Lists possible clan ranks.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/02/15
 */
public enum ClanRank {
    MEMBER(0b0_0000_0000_0000_0001_1111, "MITGLIED"),
    MODERATOR(0b0_0000_0000_0001_1111_1111, "MODERATOR"),
    ADMIN(0b0_0000_0001_1111_1111_1111, "ADMIN"),
    LEADER(0b1_1111_1111_1111_1111_1111, "LEITER");
    private final int defaultPermissionMask;
    private final String readableName;

    private ClanRank(int defaultPermissionMask, String readableName) {
        this.defaultPermissionMask = defaultPermissionMask;
        this.readableName = readableName;
    }

    /**
     * @return the default permission mask for this rank
     */
    public int getDefaultPermissionMask() {
        return defaultPermissionMask;
    }

    /**
     * @return a readable representation of this rank in German
     */
    public String getReadableName() {
        return readableName;
    }

    public static ClanRank fromString(String input) {
        switch (input.toLowerCase()) {
            case "member":
            case "mitglied":
            case "noob":
                return MEMBER;
            case "mod":
            case "moderator":
            case "idiot":
                return MODERATOR;
            case "admin":
            case "administrator":
            case "ädmin":
            case "atmien":
                return ADMIN;
            case "leader":
            case "leiter":
            case "owner":
            case "besitzer":
            case "gründer":
            case "founder":
                return LEADER;
            default:
                throw new AssertionError(input);
        }
    }
}
