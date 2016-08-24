/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute;

import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;
import li.l1t.mtc.module.chat.mute.api.MuteManager;

import javax.annotation.Nonnull;

/**
 * Chat handler for the global mute module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class MuteHandler extends AbstractChatHandler {
    private final MuteManager muteManager;

    MuteHandler(MuteManager muteManager) {
        super(ChatPhase.BLOCKING);
        this.muteManager = muteManager;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        if (muteManager.isCurrentlyMuted(evt.getPlayer())) {
            evt.tryDenyMessage(getErrorMessage(), null);
        }
    }

    @Nonnull
    private String getErrorMessage() {
        return "Du bist gemuted und kannst nicht schreiben. §a/muteinfo";
    }
}
