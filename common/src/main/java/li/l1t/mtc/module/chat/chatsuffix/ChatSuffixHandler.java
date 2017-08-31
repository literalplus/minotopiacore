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

package li.l1t.mtc.module.chat.chatsuffix;

import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;

/**
 * Chat handler for the chat suffix module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
class ChatSuffixHandler extends AbstractChatHandler {
    private final ChatSuffixModule module;

    ChatSuffixHandler(ChatSuffixModule module) {
        super(ChatPhase.INITIALISING);
        this.module = module;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        evt.appendToSuffix(module.getRepository().findChatSuffixById(evt.getPlayer().getUniqueId()));
    }
}
