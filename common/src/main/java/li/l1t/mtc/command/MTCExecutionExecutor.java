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

package li.l1t.mtc.command;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.common.cmd.XYCCommandExecutor;
import li.l1t.common.command.ExecutionExecutor;
import li.l1t.common.command.SimpleBukkitExecution;
import li.l1t.mtc.api.chat.ChatConstants;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Abstract base class for execution executors which get notified of executions via the Bukkit
 * {@link CommandExecutor} API, also providing some very commonly used utility methods.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-26
 */
public abstract class MTCExecutionExecutor extends XYCCommandExecutor implements ExecutionExecutor {
    @Override
    public boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        return execute(new SimpleBukkitExecution(sender, cmd, label, args));
    }

    /**
     * @return a new component builder adhering to the specifications of {@link
     * MessageType#RESULT_LINE}
     */
    protected XyComponentBuilder resultLineBuilder() {
        return ChatConstants.resultLineBuilder();
    }

    /**
     * @return a new component builder adhering to the specifications of {@link
     * MessageType#LIST_ITEM}
     */
    protected XyComponentBuilder listItemBuilder() {
        return ChatConstants.listItemBuilder();
    }
}
