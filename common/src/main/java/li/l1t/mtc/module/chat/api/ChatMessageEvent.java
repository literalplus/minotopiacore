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
     * @param denyingHandler the handler that attempts to deny this message, or null to suppress any
     *                       bypass notification
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
