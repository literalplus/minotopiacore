/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.command;

import li.l1t.mtc.api.module.MTCModule;
import li.l1t.mtc.api.module.ModuleCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * A module command with some MTC implementation-specific features.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-31
 */ //The whole thing would be an interface if not for Command being a class
public class MTCModuleCommand extends ModuleCommand {
    public MTCModuleCommand(MTCModule module, String name, CommandExecutor executor, String... aliases) {
        super(module, name, executor, aliases);
        addMTCBehaviours();
    }

    public MTCModuleCommand(MTCModule module, String name, CommandExecutor executor, TabCompleter completer,
                            String description, String usageMessage, String... aliases) {
        super(module, name, executor, completer, description, usageMessage, aliases);
        addMTCBehaviours();
    }

    private void addMTCBehaviours() {
        behaviour(MTCBehaviours.mtcCrediting(), MTCBehaviours.messagesChecking(), MTCBehaviours.mtcCrediting());
    }
}
