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

package li.l1t.mtc.module.chat.clanchat;

import li.l1t.mtc.clan.ClanHelper;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.clanchat.proxy.ClanSubsystemProxy;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;
import org.bukkit.entity.Player;

/**
 * Chat handler for the global mute module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class ClanChatHandler extends AbstractChatHandler {
    private final ClanSubsystemProxy proxy;

    ClanChatHandler(ClanSubsystemProxy proxy) {
        super(ChatPhase.FORWARDING);
        this.proxy = proxy;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        appendClanPrefixIfApplicable(evt);
        if (!isMessageClanScoped(evt.getPlayer(), evt.getInitialMessage())) {
            removeEscapeSequencesIfPresent(evt);
            return;
        }
        if (sentByClanMember(evt)) {
            evt.dropMessage();
            removeEscapeSequencesIfPresent(evt);
            broadcastToClanOfSender(evt);
        } else {
            evt.tryDenyMessage("Du bist in keinem Clan! §a/clan", null);
        }
    }

    private void appendClanPrefixIfApplicable(ChatMessageEvent evt) {
        evt.appendToPrefix(getProxy().getClanPrefixFor(evt.getPlayer()));
    }

    private boolean isMessageClanScoped(Player player, String message) {
        return !message.startsWith("!g") &&
                (message.startsWith("#") || ClanHelper.isInChat(player.getName()));
    }

    private void removeEscapeSequencesIfPresent(ChatMessageEvent evt) {
        String message = evt.getMessage();
        if (message.startsWith(".#") || message.startsWith("!g")) {
            evt.setMessage(message.substring(2));
        } else if (message.startsWith("#")) {
            evt.setMessage(message.substring(1));
        }
    }

    private boolean sentByClanMember(ChatMessageEvent evt) {
        return getProxy().isMemberOfAnyClan(evt.getPlayer());
    }

    private void broadcastToClanOfSender(ChatMessageEvent evt) {
        getProxy().broadcastMessageToClan(evt.getPlayer(), evt.getMessage());
    }

    private ClanSubsystemProxy getProxy() {
        return proxy;
    }
}
