/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.chat;

import li.l1t.common.util.CommandHelper;
import li.l1t.common.util.StringHelper;
import li.l1t.mtc.MTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Legacy command that allows to manage players who are muted, i.e. are not allowed to participate
 * in global chat.
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
                    target.sendMessage(String.format("%sDu wurdest von §a%s§6 entmuted.%s",
                            MTC.chatPrefix, sender.getName(), reasonString));
                }
                sender.sendMessage(String.format("%sDu hast §a%s§6 entmuted.%s",
                        MTC.chatPrefix, args[0], reasonString));
                CommandHelper.broadcast(String.format("%s§a%s§6 hat §a%s§6 entmuted.%s",
                        MTC.chatPrefix, sender.getName(), args[0], reasonString), "mtc.spy");
            } else {
                if (reason.isEmpty()) {
                    sender.sendMessage(MTC.chatPrefix + "§aDu musst einen Grund angeben!");
                    return true;
                }
                MuteHelper.mutePlayer(args[0], reason, sender.getName());
                if (target != null && target.isOnline()) {
                    target.sendMessage(String.format("%sDu wurdest von §a%s§6 gemuted. Grund: §7%s",
                            MTC.chatPrefix, sender.getName(), reason));
                }
                sender.sendMessage(String.format("%sDu hast §a%s§6 gemuted.%s",
                        MTC.chatPrefix, args[0], reason.equalsIgnoreCase("") ? "" : " Grund: §7" + reason));
                CommandHelper.broadcast(String.format("%s§a%s§6 hat §a%s§6 gemuted. Grund: §7%s",
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
                    sender.sendMessage(String.format("%sDie Seitenzahl '%s' ist keine Zahl! §a/mute list <Seite>",
                            MTC.chatPrefix, args[1]));
                    return true;
                }
            }
            sender.sendMessage("§6------ §aGemutete Spieler - Seite " + pageNum + " §6------");
            int i = 1;
            pageNum--;
            for (String path : paths) {
                if (i <= pageNum * 5 || i > (pageNum + 1) * 5) {
                    i++;
                    continue;
                }
                sender.sendMessage(String.format("§a#%d: §6%s§a %s§6 - §a von §6%s",
                        i, path, MuteHelper.getMuteTimeByPath(path), MuteHelper.getMuterByPath(path)));
                sender.sendMessage("   §awegen: §e" + MuteHelper.getReasonByPath(path));
                i++;
            }
            if ((pageNum + 1) * 5 < paths.size()) {
                sender.sendMessage("§6Nächste Seite: §a/mute list " + (pageNum + 2));
            }
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("info")) {
            String targetName = args.length > 1 ? args[1] : sender.getName();
            if (MuteHelper.isPlayerMuted(targetName)) {
                sender.sendMessage("§6------ §aMuteInfo: " + targetName + "§6 ------");
                sender.sendMessage("§6Mutegrund: §a" + MuteHelper.getReasonByPath(targetName));
                sender.sendMessage("§6gemuted von: §a" + MuteHelper.getMuterByPath(targetName));
                sender.sendMessage("§6Mutezeit: §a" + MuteHelper.getMuteTimeByPath(targetName));
            } else {
                sender.sendMessage("§6Der Spieler §a" + targetName + "§6 ist §lnicht §6gemutet.");
                return true;
            }
        } else {
            if (!CommandHelper.checkPermAndMsg(sender, "mtc.mute", label)) {
                return true;
            }
            sender.sendMessage("§cUnbekannte Aktion. Verwendung:");
            sender.sendMessage("§a/mute <Spieler> <Grund> §6Muted/Entmuted einen Spieler.");
            sender.sendMessage("§a/mute info <Spieler> §6Zeigt Muteinformationen zu einem Spieler");
            sender.sendMessage("§a/mute list [Seite] §6Listet alle aktiven Mutes auf");
        }
        return true;
    }

}
