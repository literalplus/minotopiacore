/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.command;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.cmd.XYCCommandExecutor;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.UUIDHelper;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.ExecutionExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Abstract base class for execution executors which get notified of executions via the Bukkit
 * {@link CommandExecutor} API, also providing some very commonly used utility methods.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public abstract class BukkitExecutionExecutor extends XYCCommandExecutor implements ExecutionExecutor {
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        return execute(new SimpleCommandExecution(sender, cmd, label, args));
    }

    /**
     * @param input the input string to parse an unique id from
     * @return the parsed unique id
     * @throws UserException if given input does not represent a valid unique id
     */
    protected UUID uuid(String input) {
        if (!UUIDHelper.isValidUUID(input)) {
            throw new UserException(
                    "Das ist keine UUID: %s (valide wäre zum Beispiel: %s)",
                    input, UUID.randomUUID()
            );
        } else {
            return UUIDHelper.getFromString(input);
        }
    }

    /**
     * @return a new component builder adhering to the specifications of {@link
     * MessageType#RESULT_LINE}
     */
    protected XyComponentBuilder resultLineBuilder() {
        return new XyComponentBuilder("-➩", ChatColor.YELLOW).bold(true)
                .append(" ", ChatColor.GOLD).bold(false);
    }
}
