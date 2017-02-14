/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.globalmute;

import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;

import javax.annotation.Nonnull;

/**
 * Chat handler for the global mute module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class GlobalMuteHandler extends AbstractChatHandler {
    private final GlobalMuteModule module;

    protected GlobalMuteHandler(GlobalMuteModule module) {
        super(ChatPhase.BLOCKING);
        this.module = module;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        if (module.isGlobalMute() && !evt.getPlayer().hasPermission(GlobalMuteModule.BYPASS_PERMISSION)) {
            evt.tryDenyMessage(getErrorMessage(), null);
        }
    }

    @Nonnull
    private String getErrorMessage() {
        return "Du kannst nicht schreiben, während GlobalMute an ist!" + getReasonSuffix();
    }

    private String getReasonSuffix() {
        if (module.getGlobalMuteReason().isEmpty()) {
            return "";
        }
        return " (§aGrund: " + module.getGlobalMuteReason() + "§6)";
    }
}
