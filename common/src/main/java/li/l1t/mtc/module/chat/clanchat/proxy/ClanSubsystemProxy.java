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

import org.bukkit.entity.Player;

/**
 * Proxies a clan subsystem to provide a common API for the clan chat handler to use.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public interface ClanSubsystemProxy {
    /**
     * Retrieves the clan prefix to be shown directly before the player name in chat, or an empty
     * string if the player does not belong to any clan or an error occurred reading the clan info
     *
     * @param player the player
     * @return the prefix, or an empty string if none
     */
    String getClanPrefixFor(Player player);

    /**
     * Attempts to broadcast a legacy text message to all online members of a clan.
     *
     * @param player  the player sending the message
     * @param message the message to send
     * @return whether the message was sent
     */
    boolean broadcastMessageToClan(Player player, String message);

    boolean isMemberOfAnyClan(Player player);
}
