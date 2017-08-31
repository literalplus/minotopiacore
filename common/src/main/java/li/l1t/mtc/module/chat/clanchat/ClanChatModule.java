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
    @InjectMe(failSilently = true)
    private ChatModule chatModule;
    private ClanSubsystemProxy subsystemProxy;

    protected ClanChatModule() {
        super("ClanChat", true);
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
