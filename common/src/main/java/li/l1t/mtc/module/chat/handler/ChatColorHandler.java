/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
