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

package li.l1t.mtc.module.chat.mute;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.command.CommandBehaviours;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.mute.yaml.YamlBackedMute;
import li.l1t.mtc.module.chat.mute.yaml.YamlMuteManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Provides global mute functionality for the chat module. When global mute is enabled,
 * non-permitted players are not allowed to chat.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class PlayerMuteModule extends ConfigurableMTCModule {
    @InjectMe(failSilently = true)
    private ChatModule chatModule;
    private YamlMuteManager muteManager;

    protected PlayerMuteModule() {
        super("Mute", "modules/mute.lst.yml", ClearCacheBehaviour.SAVE, true);
        ConfigurationSerialization.registerClass(YamlBackedMute.class);
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
