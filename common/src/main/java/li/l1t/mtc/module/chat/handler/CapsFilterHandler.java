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
