/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.clanchat.proxy;

import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.clan.ClanInfo;
import li.l1t.mtc.clan.ClanMemberInfo;
import org.bukkit.entity.Player;

/**
 * Wrapper proxy for the legacy clan subsystem.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class LegacyClanProxy implements ClanSubsystemProxy {
    @Override
    public String getClanPrefixFor(Player player) {
        if (!isMemberOfAnyClan(player)) {
            return "";
        }
        ClanInfo clan = findClanByPlayer(player);
        ClanMemberInfo member = findMemberInfoByPlayer(player);
        return formatClanPrefixOf(clan, member);
    }

    private ClanInfo findClanByPlayer(Player player) {
        ClanInfo clan = ClanHelper.getClanInfoByPlayerName(player.getName());
        if (clan.id < 0) { //legacy error code handling
            return null;
        }
        return clan;
    }

    private ClanMemberInfo findMemberInfoByPlayer(Player player) {
        ClanMemberInfo member = ClanHelper.getMemberInfoByPlayerName(player.getName());
        if (member.clanId < 0) {
            return null;
        }
        return member;
    }

    private String formatClanPrefixOf(ClanInfo clan, ClanMemberInfo member) {
        if (clan == null || member == null) {
            return "";
        }
        return ClanHelper.getFormattedPrefix(clan) + ClanHelper.getStarsByRank(member.getRank());
    }

    @Override
    public boolean broadcastMessageToClan(Player player, String message) {
        ClanInfo clan = findClanByPlayer(player);
        ClanMemberInfo member = findMemberInfoByPlayer(player);
        if (clan != null && member != null) {
            ClanHelper.sendChatMessage(clan, message, member);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMemberOfAnyClan(Player player) {
        return ClanHelper.isInAnyClan(player.getName());
    }
}
