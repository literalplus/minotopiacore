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
