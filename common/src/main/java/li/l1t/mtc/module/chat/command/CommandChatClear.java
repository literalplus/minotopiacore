/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
