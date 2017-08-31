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
