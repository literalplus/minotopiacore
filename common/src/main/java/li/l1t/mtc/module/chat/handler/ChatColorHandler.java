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

import li.l1t.common.util.ChatHelper;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;
import net.md_5.bungee.api.ChatColor;

/**
 * Converts alternate color codes for permitted players.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class ChatColorHandler extends AbstractChatHandler {
    private String limitedColors;

    protected ChatColorHandler() {
        super(ChatPhase.DECORATING);
    }

    @Override
    public boolean enable(ChatModule module) {
        limitedColors = module.getConfigString("color.allowed-limited", "012356789AaBbCcDdEeFfRr");
        return true;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        String message = evt.getMessage();
        if (hasPermission(evt, "mtc.chatcolor.special")) {
            evt.setMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else if (hasPermission(evt, "mtc.chatcolor")) {
            evt.setMessage(ChatHelper.convertChatColors(message, limitedColors));
        }
    }

    private boolean hasPermission(ChatMessageEvent evt, String permission) {
        return evt.getPlayer().hasPermission(permission);
    }
}
