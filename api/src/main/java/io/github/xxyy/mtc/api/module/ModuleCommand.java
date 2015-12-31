package io.github.xxyy.mtc.api.module;

import io.github.xxyy.mtc.api.command.CommandBehaviour;
import io.github.xxyy.mtc.api.command.CommandBehaviours;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A command registered by a MTC module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-28
 */
public class ModuleCommand extends Command implements PluginIdentifiableCommand {
    private final MTCModule module;
    private final CommandExecutor executor;
    private final TabCompleter completer;
    private final List<CommandBehaviour> behaviours = new ArrayList<>();

    /**
     * Creates a new module command with a default description containing the module and command name and a usage
     * message hinting that the module is probably disabled if the command is not registered. If the executor also
     * implements {@link TabCompleter}, it will be used for tab completion.
     *
     * @param module   the module registering this command
     * @param name     the name of the command as typed by users
     * @param executor the executor to use to execute the command
     * @param aliases  an array of aliases that users can use as alternatives to the name
     */
    public ModuleCommand(MTCModule module, String name, CommandExecutor executor, String... aliases) {
        this(
                module,
                name,
                executor,
                (executor instanceof TabCompleter) ? (TabCompleter) executor : null,
                String.format("%s command of MTC %s module", name, module.getName()),
                String.format("/<command> could not be executed. Is the %s module enabled?", module.getName()),
                aliases
        );
    }

    /**
     * Creates a new module command with a default description containing the module and command name and a usage
     * message hinting that the module is probably disabled if the command is not registered.
     *
     * @param module       the module registering this command
     * @param name         the name of the command as typed by users
     * @param executor     the executor to use to execute the command
     * @param completer    the completer to use for handling tab completion, or null to complete only player names
     * @param description  the command description to show in Bukkit's help system
     * @param usageMessage the usage message to display if the command couldn't be handled or the executor returns false
     * @param aliases      an array of aliases that users can use as alternatives to the name
     */
    public ModuleCommand(MTCModule module, String name, CommandExecutor executor, TabCompleter completer,
                         String description, String usageMessage, String... aliases) {
        super(name, description, usageMessage, new ArrayList<>(Arrays.asList(aliases)));
        this.module = module;
        this.executor = executor;
        this.completer = completer;
    }

    /**
     * Clears this command's behaviour list.
     *
     * @return this command, for call chaining
     */
    public ModuleCommand clearBehaviours() {
        behaviours.clear();
        return this;
    }

    /**
     * Adds behaviours to this command. Behaviours are applied before the command executor and implement common
     * pre-execution checks.
     *
     * @param behaviours the behaviours to add
     * @return this command, for call chaining
     * @see CommandBehaviours
     */
    public ModuleCommand behaviour(CommandBehaviour... behaviours) {
        this.behaviours.addAll(Arrays.asList(behaviours));
        return this;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean success;

        if (module.getPlugin() == null || !module.getPlugin().isEnabled() || !module.isEnabled()) {
            return false;
        }

        for (CommandBehaviour behaviour : behaviours) {
            if (!behaviour.apply(sender, commandLabel, this, args)) {
                return false;
            }
        }

        try {
            success = executor.onCommand(sender, this, commandLabel, args);
        } catch (Throwable ex) {
            throw new CommandException(String.format(
                    "Unhandled exception executing MTC module command '%s' in MTC %s module",
                    commandLabel, module.getName()
            ), ex);
        }

        if (!success && usageMessage.length() > 0) {
            for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
                sender.sendMessage(line);
            }
        }

        return success;
    }

    @Override
    public Plugin getPlugin() {
        return module.getPlugin();
    }

    public MTCModule getModule() {
        return module;
    }

    public List<CommandBehaviour> getBehaviours() {
        return behaviours;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException {
        List<String> completions = null;
        try {
            if (completer != null) {
                completions = completer.onTabComplete(sender, this, alias, args);
            }
        } catch (Throwable ex) {
            StringBuilder message = new StringBuilder();
            message.append("Could not tab complete '/").append(alias).append(' ');
            Arrays.stream(args)
                    .forEach(arg -> message.append(arg).append(' '));
            message.deleteCharAt(message.length() - 1).append("' in MTC module ").append(module.getName());
            throw new CommandException(message.toString(), ex);
        }

        if (completions == null) {
            return super.tabComplete(sender, alias, args);
        }
        return completions;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(super.toString());
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(", MTC ").append(module.getName()).append(')');
        return stringBuilder.toString();
    }
}
