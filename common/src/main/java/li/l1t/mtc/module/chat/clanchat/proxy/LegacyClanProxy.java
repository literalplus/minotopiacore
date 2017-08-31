/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
