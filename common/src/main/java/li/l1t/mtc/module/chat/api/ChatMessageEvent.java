/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.api;

import org.bukkit.entity.Player;

/**
 * Handles getting information about a chat message event and writing changes back to it.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public interface ChatMessageEvent {
    /**
     * Attempts to deny this chat message with an error message. This method fails if the player who
     * sent the message has the MTC global bypass permission.
     *
     * @param errorMessage   the error message to send to the player if the message is denied,
     *                       without any prefix
     * @param denyingHandler the handler that attempts to deny this message
     * @return whether the denial was successful
     */
    boolean tryDenyMessage(String errorMessage, ChatHandler denyingHandler);

    /**
     * @return whether the player may bypass filters
     */
    boolean mayBypassFilters();

    /**
     * Sends a message to this event's player, prefixed with the plugin prefix.
     *
     * @param errorMessage the message to send
     */
    void sendPrefixed(String errorMessage);

    /**
     * Marks this message for dropping, that is, no further processing will be applied to it.
     */
    void dropMessage();

    /**
     * Stops handling of this event, but without cancelling, so the message will be sent as-is.
     */
    void stopHandling();

    /**
     * Logs a warning message to the chat log.
     *
     * @param messagePattern the message to log. {@code {}} will be replaced by objects in params,
     *                       in encounter order.
     * @param params         the parameters for the message
     */
    void logWarning(String messagePattern, Object... params);

    Player getPlayer();

    String getMessage();

    String getInitialMessage();

    void setMessage(String newMessage);

    /**
     * @return the chat prefix that will be displayed before the player's name in their message
     */
    String getPrefix();

    void appendToPrefix(String toAppend);

    /**
     * @return the chat suffix that will be displayed after they player's name in their message
     */
    String getSuffix();

    void appendToSuffix(String toAppend);

    /**
     * @return whether this event should be forwarded to any further handlers
     */
    boolean shouldContinueHandling();
}
