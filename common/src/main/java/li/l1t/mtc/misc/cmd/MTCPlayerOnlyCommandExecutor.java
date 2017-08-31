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

package li.l1t.mtc.misc.cmd;

import li.l1t.common.util.CommandHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public abstract class MTCPlayerOnlyCommandExecutor extends MTCCommandExecutor {
    @Override
    public final boolean catchCommand(CommandSender sender, String senderName, Command cmd, String label, String[] args) {
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }
        Player plr = (Player) sender;
        return this.catchCommand(plr, senderName, cmd, label, args);
    }

    /**
     * Override this to catch a command that can only be executed by players.
     *
     * @param plr     Player who issued the command
     * @param plrName Name of <code>plr</code>
     * @param cmd     Command
     * @param label   Alias used
     * @param args    Arguments passed
     * @return return false to pretend this command is not loaded. This is not intended in most
     * cases, so return true.
     */
    public abstract boolean catchCommand(Player plr, String plrName, Command cmd, String label, String[] args);

}
