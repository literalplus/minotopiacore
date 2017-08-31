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

package li.l1t.mtc.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * A special behaviour for a command. Behaviours are applied before execution.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-30
 */
@FunctionalInterface
public interface CommandBehaviour {

    /**
     * Applies this behaviour to a command execution.
     *
     * @param sender the sender of this command execution
     * @param label  the alias this execution was invoked with
     * @param cmd    the command instance managing the executed command
     * @param args   the arguments passed by the sender, split at space characters
     * @return whether execution should continue
     */
    boolean apply(CommandSender sender, String label, Command cmd, String[] args);
}
