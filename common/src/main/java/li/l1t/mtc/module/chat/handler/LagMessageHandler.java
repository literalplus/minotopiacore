/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;

import java.util.regex.Pattern;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class LagMessageHandler extends AbstractChatHandler {
    private static final Pattern LAG_PATTERN = Pattern.compile("\\b([lL]+[aA4]+[gG]+)\\b");

    public LagMessageHandler() {
        super(ChatPhase.FILTERING);
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        if (!containsLagMessage(evt)) {
            return;
        }
        evt.tryDenyMessage("Bitte keine Lagnachrichten - Das hilft keinem :)", this);
        evt.logWarning("Lag message filtered: {} -> {}", evt.getPlayer(), evt.getMessage());
    }

    private boolean containsLagMessage(ChatMessageEvent evt) {
        return LAG_PATTERN.matcher(evt.getMessage()).find();
    }
}
