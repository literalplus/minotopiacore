package io.github.xxyy.mtc.module.command;

import io.github.xxyy.mtc.api.module.MTCModule;
import io.github.xxyy.mtc.api.module.ModuleCommand;
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
