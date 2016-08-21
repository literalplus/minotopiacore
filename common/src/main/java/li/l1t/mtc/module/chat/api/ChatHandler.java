/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.api;

import li.l1t.mtc.module.chat.ChatModule;

/**
 * Handles and transforms chat message events.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public interface ChatHandler {
    void handle(ChatMessageEvent evt);

    /**
     * Attempts to enable this handler, if possible.
     *
     * @param module the module managing the handler
     * @return whether the module has been enabled
     */
    boolean enable(ChatModule module);

    void disable(ChatModule module);

    /**
     * @return the phase in which this handler is to be called
     */
    ChatPhase getPhase();
}
