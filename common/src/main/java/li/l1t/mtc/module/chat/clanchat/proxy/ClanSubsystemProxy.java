/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
