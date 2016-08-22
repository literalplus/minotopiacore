/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.mtc.module.chat.ChatModule;

/**
 * Manages the list of handlers shipped with the chat module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class DefaultHandlers {
    private DefaultHandlers() {

    }

    public static void registerAllWith(ChatModule module) {
        module.registerHandler(new VaultPrefixHandler());
        module.registerHandler(new LagMessageHandler());
        module.registerHandler(new AdFilterHandler());
        module.registerHandler(new RepeatedMessageHandler());
        module.registerHandler(new CapsFilterHandler());
    }
}
