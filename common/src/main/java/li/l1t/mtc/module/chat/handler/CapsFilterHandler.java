/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.common.chat.CapsFilterService;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.AbstractChatHandler;

import java.util.Locale;

/**
 * Handles lowercasing of chat messages that contain too many uppercase characters.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-22
 */
public class CapsFilterHandler extends AbstractChatHandler {
    private CapsFilterService service;

    protected CapsFilterHandler() {
        super(ChatPhase.CENSORING);
    }

    @Override
    public boolean enable(ChatModule module) {
        float capsFactor = ((float) module.getConfigInt("caps.max-percent-caps", 50)) / 100F;
        int ignoreUntilLength = module.getConfigInt("caps.ignore-messages-shorter-than-characters", 5);
        service = new CapsFilterService(capsFactor, ignoreUntilLength);
        return true;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        if (service.check(evt.getMessage()) && !evt.mayBypassFilters()) {
            evt.setMessage(evt.getMessage().toLowerCase(Locale.GERMAN));
            evt.sendPrefixed("Bitte nicht alles großschreiben :)");
        }
    }
}
