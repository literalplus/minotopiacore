/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.api;

import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Dispatches chat events to registered handlers and constructs the final message.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public interface ChatDispatcher {
    /**
     * Registers and enables a handler.
     *
     * @param handler the handler to register
     * @return whether registration and enabling succeeded
     */
    boolean registerHandler(ChatHandler handler);

    /**
     * Unregisters and disables a handler.
     *
     * @param handler the handler
     */
    void unregisterHandler(ChatHandler handler);

    void dispatchEvent(AsyncPlayerChatEvent event);
}
