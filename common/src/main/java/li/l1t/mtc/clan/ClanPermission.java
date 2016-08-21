/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.clan;

import li.l1t.mtc.MTC;
import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.command.CommandSender;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30.6.14
 */
public enum ClanPermission {
    //LEADER
    RESET(0b1_0000_0000_0000_0000_0000),
    REMOVE(0b0_1000_0000_0000_0000_0000),
    SETOPTION(0b0_0100_0000_0000_0000_0000),
    ALLIANCEENEMY(0b0_0010_0000_0000_0000_0000),
    WAR(0b0_0001_0000_0000_0000_0000),
    BUY(0b0_0000_1000_0000_0000_0000),
    IGNOREKICK(0b0_0000_0100_0000_0000_0000),
    SETRANK(0b0_0000_0010_0000_0000_0000),
    //ADMIN
    SETSEARCH(0b0_0000_0001_0000_0000_0000),
    KICK(0b0_0000_0000_1000_0000_0000),
    SETBASE(0b0_0000_0000_0100_0000_0000),
    BANKGET(0b0_0000_0000_0010_0000_0000),
    //MODERATOR
    INVITE(0b0_0000_0000_0001_0000_0000),
    REVOKE(0b0_0000_0000_0000_1000_0000),
    CHATCOLSPECIAL(0b0_0000_0000_0000_0100_0000),
    MUTECHAT(0b0_0000_0000_0000_0010_0000),
    //MEMBER
    CHEST(0b0_0000_0000_0000_0001_0000),
    CHATCOL(0b0_0000_0000_0000_0000_1000),
    TPBASE(0b0_0000_0000_0000_0000_0100),
    USECHAT(0b0_0000_0000_0000_0000_0010);

    public static final int LEADER_PERMISSIONS = 0b1_1111_1111_1111_1111_1111;
    public static final int ADMIN_PERMISSIONS = 0b0_0000_0001_1111_1111_1111;
    public static final int MODERATOR_PERMISSIONS = 0b0_0000_0000_0001_1111_1111;
    public static final int MEMBER_PERMISSIONS = 0b0_0000_0000_0000_0001_1111;

    private int bitValue;

    ClanPermission(int bitValue) {
        this.bitValue = bitValue;
    }

    public static int getDefaultPermissionsByRank(ClanMemberInfo.ClanRank rank) {
        switch (rank) {
            case LEADER:
                return LEADER_PERMISSIONS;
            case ADMIN:
                return ADMIN_PERMISSIONS;
            case MODERATOR:
                return MODERATOR_PERMISSIONS;
            default:
                return MEMBER_PERMISSIONS;
        }
    }

    public static boolean has(ClanMemberInfo cmi, ClanPermission perm) {
        return has(cmi.userPermissions, perm);
    }

    /**
     * Checks if a permission integer has been flagged to have <code>perm</code>.
     *
     * @param compareValue permission integer
     * @param perm         Permission to check for
     * @return if the integer is marked with this permission
     */
    public static boolean has(int compareValue, ClanPermission perm) {
        return (perm.bitValue() & compareValue) != 0;
    }

    public static boolean hasAndMessage(ClanMemberInfo cmi, ClanPermission perm, CommandSender sender) {
        return (has(cmi, perm) || (sender.hasPermission("mtc.clana.override"))) //this expr is whether the user has permission - (has clan perm | has override)
                || !MTCHelper.sendLocArgs("XC-noperm", sender, true, perm.toString(), MTC.codeChatCol); //This always returns true (true && true -> true/false && true -> false)
    }

    /**
     * Checks if a player has a permission. Also sends a message if not.
     *
     * @param sender Receiver of the message and also subject of the check.
     * @param perm   Permission to seek.
     * @return if.
     * @see #has(ClanMemberInfo, ClanPermission)
     */
    public static boolean hasAndMessage(CommandSender sender, ClanPermission perm) {
        String senderName = sender.getName();
        if (!ClanHelper.isInAnyClan(senderName)) {
            return false;
        }
        ClanMemberInfo cmi = ClanHelper.getMemberInfoByPlayerName(senderName);
        if (cmi.clanId < 0) {
            return MTCHelper.sendLocArgs("XC-cmifetcherr", sender, true, cmi.clanId);
        }
        return hasAndMessage(cmi, perm, sender);
    }

    public int bitValue() {
        return bitValue;
    }
}
