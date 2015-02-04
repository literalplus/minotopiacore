/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.clan.api;

import org.bukkit.command.CommandSender;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.helper.MTCHelper;

/**
 * Lists clan permissions
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 30.6.14
 */
public enum ClanPermission {
    //@formatter:off
    //LEADER
    RESET         (0b1_0000_0000_0000_0000_0000), //Not sure if this is necessary - unused
    REMOVE        (0b0_1000_0000_0000_0000_0000),
    SETOPTION     (0b0_0100_0000_0000_0000_0000),
    ALLIANCEENEMY (0b0_0010_0000_0000_0000_0000),
    WAR           (0b0_0001_0000_0000_0000_0000),
    BUY           (0b0_0000_1000_0000_0000_0000), //What is this? - unused
    IGNOREKICK    (0b0_0000_0100_0000_0000_0000), //Shouldn't be necessary - unused
    SETRANK       (0b0_0000_0010_0000_0000_0000),
    //ADMIN
    SETSEARCH     (0b0_0000_0001_0000_0000_0000), //No longer planned - unused
    KICK          (0b0_0000_0000_1000_0000_0000),
    SETBASE       (0b0_0000_0000_0100_0000_0000),
    BANKGET       (0b0_0000_0000_0010_0000_0000),
    //MODERATOR
    INVITE        (0b0_0000_0000_0001_0000_0000),
    REVOKE        (0b0_0000_0000_0000_1000_0000),
    CHATCOLSPECIAL(0b0_0000_0000_0000_0100_0000),
    MUTECHAT      (0b0_0000_0000_0000_0010_0000), //No longer planned - unused
    //MEMBER
    CHEST         (0b0_0000_0000_0000_0001_0000), //No longer planned - unused
    CHATCOL       (0b0_0000_0000_0000_0000_1000),
    TPBASE        (0b0_0000_0000_0000_0000_0100),
    USECHAT       (0b0_0000_0000_0000_0000_0010);
    //@formatter:on

    private int bitValue;

    ClanPermission(int bitValue) {
        this.bitValue = bitValue;
    }

    public boolean has(ClanMember member) {
        return has(member.getPermissionMask());
    }

    public boolean has(int compareValue) {
        return (bitValue() & compareValue) != 0;
    }

    public boolean hasAndMessage(ClanMember member, CommandSender sender) {
        return (has(member) || (sender.hasPermission("mtc.clana.override"))) //this expr is whether the user has permission - (has clan perm | has override)
                || !MTCHelper.sendLocArgs("XC-noperm", sender, true, this.toString(), MTC.codeChatCol); //This always returns true (true && true -> true/false && true -> false)
    }

    public int bitValue() {
        return bitValue;
    }
}
