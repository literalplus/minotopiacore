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
