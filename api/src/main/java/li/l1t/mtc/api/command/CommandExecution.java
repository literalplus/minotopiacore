/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.command;

import li.l1t.common.exception.UserException;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.api.chat.MessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

/**
 * Stores basic information about an individual execution of a command.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public interface CommandExecution {
    /**
     * @return the sender that invoked this execution
     */
    CommandSender sender();

    /**
     * @return a unique id representing the command sender, according to the specifications detailed
     * in {@link li.l1t.common.util.CommandHelper#getSenderId(CommandSender)}
     */
    UUID senderId();

    /**
     * @return the {@link CommandSender#getName()} executing the command
     */
    String senderName();

    /**
     * @return the array of space-separated arguments passed to the command
     */
    String[] args();

    /**
     * @param index the index of the argument to operate on
     * @return whether this execution has an argument at given index
     */
    boolean hasArg(int index);

    /**
     * @return whether no arguments were passed to this execution
     */
    boolean hasNoArgs();

    /**
     * @param index the zero-based index of the {@link #args() argument} to access
     * @return the argument at given index
     * @throws MissingArgumentException if there is no argument at that index
     */
    String arg(int index);

    /**
     * @param index the zero-based index of the {@link #args() argument} to access
     * @return the integer argument at given index
     * @throws MissingArgumentException if there is no argument at that index
     * @throws UserException            if the argument at given index is not an integer
     */
    int intArg(int index);

    /**
     * @param index the zero-based index of the {@link #args() argument} to access
     * @return the UUID argument at given index
     * @throws MissingArgumentException if there is no argument at that index
     * @throws UserException            if the argument at given index is not a UUID
     */
    UUID uuidArg(int index);

    /**
     * @param index the zero-based index of the {@link #args() argument} to find
     * @return an optional containing the argument at given index, or an empty optional if there is
     * no argument at that index
     */
    Optional<String> findArg(int index);

    /**
     * @param startIndex the index of the first argument to process
     * @return the space-separated string of arguments passed to this execution
     */
    String joinedArgs(int startIndex);

    /**
     * @param startIndex the index of the first argument to process
     * @return the space-separated string of arguments passed to this execution, with {@link
     * StringHelper#varArgsString(String[], int, boolean) alternate color codes} replaced
     */
    String joinedArgsColored(int startIndex);

    /**
     * @return the command managing this execution
     */
    Command command();

    /**
     * @return the alias the command was executed with
     */
    String label();

    /**
     * Sends a message to the command sender.
     *
     * @param message the message pattern, possibly using the format expressions specified in {@link
     *                MessageType#format(String, Object...)}
     * @param params  the arguments for the pattern, will be inserted into the pattern like {@link
     *                String#format(String, Object...)} does
     */
    void respond(String message, Object... params);

    /**
     * Sends a message to the command sender, formatting it according to the specifications
     * associated with given message type.
     *
     * @param type    the message type to use for formatting
     * @param message the message pattern, possibly using the format expressions specified in {@link
     *                MessageType#format(String, Object...)}
     * @param params  the arguments for the pattern, will be inserted into the pattern like {@link
     *                String#format(String, Object...)} does
     */
    void respond(MessageType type, String message, Object... params);

    /**
     * Sends a message to the command sender, first converting it to legacy text if the sender is
     * not a player.
     *
     * @param message the message to send
     */
    void respond(BaseComponent[] message);

    /**
     * Sends a message to the command sender, first converting it to legacy text if the sender is
     * not a player.
     *
     * @param messageBuilder the builder that will be used to obtain the message to send
     */
    void respond(ComponentBuilder messageBuilder);

    /**
     * Sends a message to the command sender, indicating the intended usage of given sub command
     * using given description and details.
     *
     * @param subCommand  the sub command the usage refers to, or an empty string for the main
     *                    command
     * @param arguments   the standardised description of the arguments required for given sub
     *                    command
     * @param description a short ellipsis of what given sub command does
     */
    void respondUsage(String subCommand, String arguments, String description);

    /**
     * @throws PlayerOnlyException if the sender is not an instance of {@link org.bukkit.entity.Player}
     */
    void requireIsPlayer() throws PlayerOnlyException;

    /**
     * @param permission the permission required to proceed
     * @throws UserPermissionException if the sender does not have given permission
     */
    void requirePermission(String permission) throws UserPermissionException;
}
