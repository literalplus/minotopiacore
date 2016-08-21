/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.impl;

import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatHandler;
import li.l1t.mtc.module.chat.api.ChatPhase;

/**
 * Abstract base class for chat handlers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public abstract class AbstractChatHandler implements ChatHandler {
    private final ChatPhase phase;

    protected AbstractChatHandler(ChatPhase phase) {
        this.phase = phase;
    }

    @Override
    public boolean enable(ChatModule module) {
        return true;
    }

    @Override
    public void disable(ChatModule module) {
        //no-op by default
    }

    @Override
    public ChatPhase getPhase() {
        return phase;
    }
}
