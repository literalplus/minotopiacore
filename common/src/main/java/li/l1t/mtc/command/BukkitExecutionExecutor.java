/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.command;

import li.l1t.common.cmd.XYCCommandExecutor;
import li.l1t.mtc.api.command.ExecutionExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Abstract base class for execution executors which get notified of executions via the Bukkit
 * {@link CommandExecutor} API.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public abstract class BukkitExecutionExecutor extends XYCCommandExecutor implements ExecutionExecutor {
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        return execute(new SimpleCommandExecution(sender, cmd, label, args));
    }
}
