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

package li.l1t.mtc.module.chat.chatsuffix;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.MTCModuleAdapter;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.chatsuffix.data.CachingChatSuffixRepository;
import li.l1t.mtc.module.chat.chatsuffix.data.ChatSuffixRepository;
import li.l1t.mtc.module.chat.chatsuffix.data.SqlChatSuffixRepository;

/**
 * Provides a chat suffix feature for the chat module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class ChatSuffixModule extends MTCModuleAdapter {
    @InjectMe(failSilently = true)
    private ChatModule chatModule;
    private boolean globalMute = false;
    private String globalMuteReason = "";
    private ChatSuffixRepository repository;

    protected ChatSuffixModule() {
        super("Chatfarbe", true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        repository = new CachingChatSuffixRepository(new SqlChatSuffixRepository(plugin.getSql()));
        chatModule.registerHandler(new ChatSuffixHandler(this));
        registerCommand(new CommandChatSuffix(this), "chatfarbe", "cf")
                .behaviour(CommandBehaviours.permissionChecking("mtc.chatfarbe.change"));
        registerListener(new ChatSuffixJoinConverter(this));
    }

    public boolean isGlobalMute() {
        return globalMute;
    }

    public void toggleGlobalMute(String reason) {
        setGlobalMute(!globalMute, reason);
    }

    public void setGlobalMute(boolean globalMute, String reason) {
        setGlobalMute(globalMute);
        setGlobalMuteReason(reason);
    }

    private void setGlobalMute(boolean globalMute) {
        this.globalMute = globalMute;
        setGlobalMuteReason(null);
    }

    public void enableGlobalMute(String reason) {
        setGlobalMute(true, reason);
    }

    public void disableGlobaleMute() {
        setGlobalMute(false);
    }

    private void setGlobalMuteReason(String reason) {
        if (reason == null) {
            reason = "";
        }
        this.globalMuteReason = reason;
    }

    public String getGlobalMuteReason() {
        return globalMuteReason;
    }

    @Override
    public void clearCache(boolean forced, MTCPlugin plugin) {
        repository.clearCache();
    }

    public ChatSuffixRepository getRepository() {
        return repository;
    }
}
