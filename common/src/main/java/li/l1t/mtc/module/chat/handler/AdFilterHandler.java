/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.common.chat.AdFilterService;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.ModuleAwareChatHandler;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class AdFilterHandler extends ModuleAwareChatHandler {
    private AdFilterService filterService;

    public AdFilterHandler() {
        super(ChatPhase.CENSORING);
    }

    @Override
    public boolean enable(ChatModule module) {
        super.enable(module);
        boolean findHiddenDots = module.getConfigBoolean("ads.aggressive-dot-matching", true);
        boolean findIpAddresses = module.getConfigBoolean("ads.match-ip-addresses", true);
        module.getConfigStringList("ads.ignored-domains", "minotopia.me", "l1t.li");
        filterService = new AdFilterService();
        filterService.setFindHiddenDots(findHiddenDots);
        filterService.setFindIpAddresses(findIpAddresses);
        return true;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        if (filterService.test(evt.getInitialMessage())) {
            handleMatch(evt);
        }
    }

    private void handleMatch(ChatMessageEvent evt) {
        if (evt.mayBypassFilters()) {
            evt.sendPrefixed("Na leiwand, Werbefilter ignoriert.");
            return;
        }
        evt.sendPrefixed("§cWerbung ist ein Armutszeugnis.");
        evt.setMessage("MinoTopia ist mein Lieblingsserver! <3");
        broadcastAdInfo("§a%s§6 hat den Werbefilter ausgelöst:", evt.getPlayer().getName());
        broadcastAdInfo("§a   %s", evt.getInitialMessage());
    }

    private void broadcastAdInfo(String message, Object... params) {
        CommandHelper.broadcast(getModule().formatMessage(message, params), "mtc.adinfo");
    }
}
