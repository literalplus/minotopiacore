/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
        super(ChatPhase.BLOCKING);
        this.proxy = proxy;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        appendClanPrefixIfApplicable(evt);
        if (!isMessageClanScoped(evt.getPlayer(), evt.getInitialMessage())) {
            removeEscapeSequenceIfPresent(evt);
            return;
        }
        if (sentByClanMember(evt)) {
            evt.dropMessage();
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

    private void removeEscapeSequenceIfPresent(ChatMessageEvent evt) {
        if (evt.getMessage().startsWith(".#")) {
            evt.setMessage(evt.getMessage().replaceFirst("\\.#", "#"));
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
