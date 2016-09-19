/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import com.google.common.base.Preconditions;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Manages command registration for commands registered with the MTC command API. This interfaces
 * directly with the server's command map using Reflection, but stores the instance once retrieved.
 * That means that one instance can only be used for commands from the same server.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
public class CommandRegistrationManager {
    private CommandMap commandMap;
    private Map<String, Command> knownCommands;
    private Server server;

    /**
     * Attempts to register a command with the corresponding server's command map. Note that this
     * method uses Reflection to access the command map and may fail due to that. The command map is
     * cached once retrieved.
     *
     * @param command the command to register
     * @return whether the command was registered under its default label
     * @throws IllegalStateException if retrieval of the command map fails
     */
    public <T extends Command & PluginIdentifiableCommand> boolean registerCommand(T command) throws IllegalStateException {
        return getCommandMap(command.getPlugin().getServer()).register("mtc", command);
    }

    /**
     * Retrieves a server's command map or serves the one cached in this manger.
     *
     * @param server the server to find the command map for
     * @return the command map
     * @throws IllegalStateException if the command map could not be retrieved
     */
    public CommandMap getCommandMap(Server server) throws IllegalStateException {
        checkServer(server);
        if (commandMap == null) {
            this.server = server;
            try {
                Field commandMapField = server.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(server);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Failed to retrieve command map for MTC registrations", e);
            }
        }

        return commandMap;
    }

    private void checkServer(Server server) {
        Preconditions.checkArgument(this.server == null || server == this.server, "Server does not match manager's server!");
    }

    /**
     * Unregisters a command and all aliases by label.
     *
     * @param server the server to operate on
     * @param label  the label of the command to unregister
     * @return the unregistered command or null if no such command existed
     * @throws IllegalStateException if an error occurs accessing the internals of Bukkit's command
     *                               map
     */
    public Command unregisterCommandAndAliases(Server server, String label) throws IllegalStateException {
        Preconditions.checkNotNull(label, "label");
        Map<String, Command> map = getKnownCommandsMap(getCommandMap(server));
        Command command = map.get(label);
        if (command == null) {
            return null;
        }
        map.values().removeIf(command::equals);
        return command;
    }

    /**
     * Unregisters a command by its label. Does not unregister its aliases.
     *
     * @param server the server to operate on
     * @param label  the label of the command to unregister
     * @return the unregistered command or null if no such command existed
     * @throws IllegalStateException if an error occurs accessing the internals of Bukkit's command
     *                               map
     */
    public Command unregisterCommandLabel(Server server, String label) throws IllegalStateException {
        Preconditions.checkNotNull(label, "label");
        return getKnownCommandsMap(getCommandMap(server)).remove(label);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Command> getKnownCommandsMap(CommandMap commandMap) {
        Preconditions.checkNotNull(commandMap, "commandMap");
        if (knownCommands == null) {
            try {
                Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Failed to retrieve internal command map for MTC registrations", e);
            }
        }
        return knownCommands;
    }

    /**
     * @return the server whose command map this manager is accessing or null if none
     */
    public Server getServer() {
        return server;
    }
}
