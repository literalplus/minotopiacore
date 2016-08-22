/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.chatsuffix;

import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;

/**
 * Chat handler for the chat suffix module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class ChatSuffixHandler extends AbstractChatHandler {
    private final ChatSuffixModule module;

    ChatSuffixHandler(ChatSuffixModule module) {
        super(ChatPhase.INITIALISING);
        this.module = module;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        evt.appendToSuffix(module.getRepository().findChatSuffixById(evt.getPlayer().getUniqueId()));
    }
}
