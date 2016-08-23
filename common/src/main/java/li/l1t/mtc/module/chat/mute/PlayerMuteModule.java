/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.mute;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.mute.yaml.YamlMuteManager;

/**
 * Provides global mute functionality for the chat module. When global mute is enabled,
 * non-permitted players are not allowed to chat.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class PlayerMuteModule extends ConfigurableMTCModule {
    @InjectMe
    private ChatModule chatModule;
    private YamlMuteManager muteManager;

    protected PlayerMuteModule() {
        super("Mute", "modules/mute.lst.yml", ClearCacheBehaviour.SAVE, true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        muteManager = new YamlMuteManager(configuration, plugin);
        chatModule.registerHandler(new MuteHandler(muteManager));
        registerCommands();
    }

    private void registerCommands() {
        XLoginHook xLoginHook = getPlugin().getXLoginHook();
        registerCommand(new CommandMute(xLoginHook, muteManager), "mute")
                .behaviour(CommandBehaviours.permissionChecking("mtc.mute"));
        registerCommand(new CommandMuteInfo(xLoginHook, muteManager), "muteinfo");
        registerCommand(new CommandUnmute(xLoginHook, muteManager), "unmute")
                .behaviour(CommandBehaviours.permissionChecking("mtc.mute"));
    }

    @Override
    protected void reloadImpl() {
        //mute manager writes on demand
    }
}
