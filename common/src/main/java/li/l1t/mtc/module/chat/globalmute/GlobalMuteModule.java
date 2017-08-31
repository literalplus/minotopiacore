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

package li.l1t.mtc.module.chat.globalmute;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.MTCModuleAdapter;
import li.l1t.mtc.module.chat.ChatModule;

/**
 * Provides global mute functionality for the chat module. When global mute is enabled,
 * non-permitted players are not allowed to chat.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class GlobalMuteModule extends MTCModuleAdapter {
    public static final String BYPASS_PERMISSION = "mtc.globalmute.exempt";
    @InjectMe(failSilently = true)
    private ChatModule chatModule;
    private boolean globalMute = false;
    private String globalMuteReason = "";

    protected GlobalMuteModule() {
        super("GlobalMute", true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        chatModule.registerHandler(new GlobalMuteHandler(this));
        registerCommand(new CommandGlobalMute(this), "globalmute", "glomu")
                .behaviour(CommandBehaviours.permissionChecking("mtc.globalmute.toggle"));
    }

    public boolean isGlobalMute() {
        return globalMute;
    }

    private void setGlobalMute(boolean globalMute) {
        this.globalMute = globalMute;
        setGlobalMuteReason(null);
    }

    public void toggleGlobalMute(String reason) {
        setGlobalMute(!globalMute, reason);
    }

    public void setGlobalMute(boolean globalMute, String reason) {
        setGlobalMute(globalMute);
        setGlobalMuteReason(reason);
    }

    public void enableGlobalMute(String reason) {
        setGlobalMute(true, reason);
    }

    public void disableGlobaleMute() {
        setGlobalMute(false);
    }

    public String getGlobalMuteReason() {
        return globalMuteReason;
    }

    private void setGlobalMuteReason(String reason) {
        if (reason == null) {
            reason = "";
        }
        this.globalMuteReason = reason;
    }
}
