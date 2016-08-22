/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
    @InjectMe
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
