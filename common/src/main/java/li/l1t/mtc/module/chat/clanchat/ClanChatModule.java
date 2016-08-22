/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.clanchat;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.MTCModuleAdapter;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.clanchat.proxy.ClanSubsystemProxy;
import li.l1t.mtc.module.chat.clanchat.proxy.LegacyClanProxy;

/**
 * Provides integration between the legacy clan subsystem and the chat module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class ClanChatModule extends MTCModuleAdapter {
    @InjectMe
    private ChatModule chatModule;
    private ClanSubsystemProxy subsystemProxy;

    protected ClanChatModule() {
        super("GlobalMute", true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        subsystemProxy = new LegacyClanProxy(); //this will have logic once we have a clan module
        chatModule.registerHandler(new ClanChatHandler(getSubsystemProxy()));
    }

    public ClanSubsystemProxy getSubsystemProxy() {
        return subsystemProxy;
    }
}
