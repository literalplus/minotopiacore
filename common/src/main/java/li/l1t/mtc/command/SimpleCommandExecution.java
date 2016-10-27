/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.command;

import com.google.common.base.Preconditions;
import li.l1t.common.chat.ComponentSender;
import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.api.command.MissingArgumentException;
import li.l1t.mtc.api.command.PlayerOnlyException;
import li.l1t.mtc.api.command.UserPermissionException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

/**
 * A simple implementation of a {@link CommandExecution}, holding metadata about a command
 * execution.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public class SimpleCommandExecution implements CommandExecution {
    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] args;

    public SimpleCommandExecution(CommandSender sender, Command command, String label, String[] args) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.command = Preconditions.checkNotNull(command, "command");
        this.label = Preconditions.checkNotNull(label, "label");
        this.args = Preconditions.checkNotNull(args, "args");
    }

    @Override
    public CommandSender sender() {
        return sender;
    }

    @Override
    public UUID senderId() {
        return CommandHelper.getSenderId(sender);
    }

    @Override
    public String senderName() {
        return sender.getName();
    }

    @Override
    public String[] args() {
        return args;
    }

    @Override
    public String arg(int index) {
        if (!hasArg(index)) {
            throw MissingArgumentException.forIndex(index);
        }
        return args[index];
    }

    @Override
    public int intArg(int index) {
        try {
            return Integer.parseInt(arg(index));
        } catch (NumberFormatException e) {
            throw new UserException("Argument %d: Das ist keine Zahl: '%s'", index + 1, arg(index));
        }
    }

    @Override
    public UUID uuidArg(int index) {
        try {
            return UUID.fromString(arg(index));
        } catch (IllegalArgumentException e) {
            throw new UserException(
                    "Argument %d: Das ist keine UUID: '%s'. Eine valide UUID ist zum Beispiel '%s'.",
                    index + 1, arg(index), UUID.randomUUID()
            );
        }
    }

    @Override
    public Optional<String> findArg(int index) {
        return hasArg(index) ? Optional.of(arg(index)) : Optional.empty();
    }

    @Override
    public boolean hasArg(int index) {
        return args.length > index;
    }

    @Override
    public boolean hasNoArgs() {
        return args.length == 0;
    }

    @Override
    public String joinedArgs(int startIndex) {
        return StringHelper.varArgsString(args(), startIndex, false);
    }

    @Override
    public String joinedArgsColored(int startIndex) {
        return StringHelper.varArgsString(args(), startIndex, true);
    }

    @Override
    public Command command() {
        return command;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public void respond(String message, Object... params) {
        sender.sendMessage(String.format(
                ChatConstants.convertCustomColorCodes(message),
                params
        ));
    }

    @Override
    public void respond(MessageType type, String message, Object... params) {
        type.sendTo(sender, message, params);
    }

    @Override
    public void respond(BaseComponent[] message) {
        ComponentSender.sendTo(message, sender);
    }

    @Override
    public void respond(ComponentBuilder messageBuilder) {
        ComponentSender.sendTo(messageBuilder, sender);
    }

    @Override
    public void respondUsage(String subCommand, String arguments, String description) {
        String commandLine = formatCommandLineForSub(subCommand);
        respond(
                new XyComponentBuilder(commandLine, ChatColor.YELLOW)
                        .suggest(commandLine)
                        .tooltip("§eKlicken zum Kopieren:\n" + commandLine)
                        .appendIf(!arguments.isEmpty(), " " + arguments)
                        .append(" ", ComponentBuilder.FormatRetention.NONE)
                        .append(description, ChatColor.GOLD)
        );
    }

    private String formatCommandLineForSub(String subCommand) {
        if (subCommand.isEmpty()) {
            return "/" + label();
        } else {
            return "/" + label() + " " + subCommand;
        }
    }

    @Override
    public void requireIsPlayer() throws PlayerOnlyException {
        PlayerOnlyException.checkIsPlayer(sender(), "/%s %s", label, joinedArgs(0));
    }

    @Override
    public void requirePermission(String permission) throws UserPermissionException {
        UserPermissionException.checkPermission(sender(), permission);
    }
}
