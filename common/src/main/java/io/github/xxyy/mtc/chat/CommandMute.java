/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.mtc.MTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Legacy command that allows to manage players who are muted, i.e. are not allowed to participate in global chat.
 *
 * @author xxyy
 */
public final class CommandMute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && !(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("list") ||
                args[0].equalsIgnoreCase("info"))) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            String reason = StringHelper.varArgsString(args, 1, true);

            if (MuteHelper.isPlayerMuted(args[0])) {
                String reasonString = reason.isEmpty() ? "" : " Grund: §7" + reason;
                MuteHelper.unmutePlayer(args[0]);

                if (target != null && target.isOnline()) {
                    target.sendMessage(String.format("%sDu wurdest von §b%s§6 entmuted.%s",
                            MTC.chatPrefix, sender.getName(), reasonString));
                }
                sender.sendMessage(String.format("%sDu hast §b%s§6 entmuted.%s",
                        MTC.chatPrefix, args[0], reasonString));
                CommandHelper.broadcast(String.format("%s§b%s§6 hat §b%s§6 entmuted.%s",
                        MTC.chatPrefix, sender.getName(), args[0], reasonString), "mtc.spy");
            } else {
                if (reason.isEmpty()) {
                    sender.sendMessage(MTC.chatPrefix + "§cDu musst einen Grund angeben!");
                    return true;
                }
                MuteHelper.mutePlayer(args[0], reason, sender.getName());
                if (target != null && target.isOnline()) {
                    target.sendMessage(String.format("%sDu wurdest von §b%s§6 gemuted. Grund: §7%s",
                            MTC.chatPrefix, sender.getName(), reason));
                }
                sender.sendMessage(String.format("%sDu hast §b%s§6 gemuted.%s",
                        MTC.chatPrefix, args[0], reason.equalsIgnoreCase("") ? "" : " Grund: §7" + reason));
                CommandHelper.broadcast(String.format("%s§b%s§6 hat §b%s§6 gemuted. Grund: §7%s",
                        MTC.chatPrefix, sender.getName(), args[0], reason), "mtc.spy");
            }
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
            Set<String> paths = MuteHelper.getMutedPlayerPaths();
            if (paths == null || paths.size() == 0) {
                sender.sendMessage(MTC.chatPrefix + "Es sind keine Spieler gemuted!");
                return true;
            }
            int pageNum = 1;
            if (args.length == 2) {
                try {
                    pageNum = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(String.format("%sDie Seitenzahl '%s' ist keine Zahl! §b/mute list <Seite>",
                            MTC.chatPrefix, args[1]));
                    return true;
                }
            }
            sender.sendMessage("§6------ §bGemutete Spieler - Seite " + pageNum + " §6------");
            int i = 1;
            pageNum--;
            for (String path : paths) {
                if (i <= pageNum * 5 || i > (pageNum + 1) * 5) {
                    i++;
                    continue;
                }
                sender.sendMessage(String.format("§b#%d: §6%s§b %s§6 - §b von §6%s",
                        i, path, MuteHelper.getMuteTimeByPath(path), MuteHelper.getMuterByPath(path)));
                sender.sendMessage("   §bwegen: §e" + MuteHelper.getReasonByPath(path));
                i++;
            }
            if ((pageNum + 1) * 5 < paths.size()) {
                sender.sendMessage("§6Nächste Seite: §b/mute list " + (pageNum + 2));
            }
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("info")) {
            String tgtName = args[1];
            if (args.length < 2) {
                args[1] = sender.getName();
            }
            if (MuteHelper.isPlayerMuted(tgtName)) {
                sender.sendMessage("§6------ §bMuteInfo: " + tgtName + "§6 ------");
                sender.sendMessage("§6Mutegrund: §e" + MuteHelper.getReasonByPath(tgtName));
                sender.sendMessage("§6gemuted von: §b" + MuteHelper.getMuterByPath(tgtName));
                sender.sendMessage("§6Mutezeit: §e" + MuteHelper.getMuteTimeByPath(tgtName));
            } else {
                sender.sendMessage("§6Der Spieler §b" + tgtName + "§6 ist §lnicht §6gemutet.");
                return true;
            }
        } else {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
            sender.sendMessage("§cUnbekannte Aktion. Verwendung:");
            sender.sendMessage("§b/mute <Spieler> <Grund> §6Muted/Entmuted einen Spieler.");
            sender.sendMessage("§b/mute info <Spieler> §6Zeigt Muteinformationen zu einem Spieler");
            sender.sendMessage("§b/mute list [Seite] §6Listet alle aktiven Mutes auf");
        }
        return true;
    }

}
