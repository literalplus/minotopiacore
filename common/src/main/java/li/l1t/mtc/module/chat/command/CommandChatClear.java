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

package li.l1t.mtc.module.chat.command;

import li.l1t.common.util.StringHelper;
import li.l1t.mtc.module.chat.ChatModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Simple command that allows to clear the global chat for non-permitted players.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class CommandChatClear implements CommandExecutor {
    private final ChatModule module;

    public CommandChatClear(ChatModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getOnlinePlayers().forEach(player -> clearPlayerChatIfNotExempt(player, sender));
        Bukkit.broadcastMessage(getBroadcastMessage(args));
        return true;
    }

    private void clearPlayerChatIfNotExempt(Player player, CommandSender culprit) {
        if (player.hasPermission("mtc.chatclear.exempt")) {
            sendPrefixed(player, "§a" + culprit.getName() + " §6hat den globalen Chat geleert.");
            return;
        }
        for (int i = 0; i < 150; i++) {
            player.sendMessage(" §r ");     //Apparently this helps with hack clients' message combining stuffs which are apparently 9001% exploits
            player.sendMessage("    §3  "); //Even though one could just use the logs, but yeah, it looks professional or something. Yell at Janmm14.
        }
    }

    private void sendPrefixed(Player player, String message) {
        player.sendMessage(module.formatMessage(message));
    }

    private String getBroadcastMessage(String[] args) {
        return module.formatMessage("Der globale Chat wurde geleert." + formatReasonSuffix(args));
    }

    private String formatReasonSuffix(String[] args) {
        String reason = StringHelper.varArgsString(args, 0, true);
        if (reason.isEmpty()) {
            return "";
        }
        return " §aGrund: " + reason;
    }
}
