package io.github.xxyy.minotopiacore.bans.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.bans.BanInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;


public class CommandBanInfo_ implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.ban.info", label)) {
            return true;
        }
        if (args.length >= 1 && !args[0].equalsIgnoreCase("help")) {
            BanInfo bi = BanHelper.getBanInfoByPlayerName(args[0].toLowerCase());
            if (bi.id == -2) {
                sender.sendMessage(MTC.banChatPrefix + "§e" + args[0] + "§c ist nicht gebannt!");
                return true;
            }
            if (bi.id == -3) {
                sender.sendMessage(MTC.banChatPrefix + "SQL-Fehler! <3");
                return true;
            }
            if (bi.id == -1) {
                sender.sendMessage(MTC.banChatPrefix + "Allgemeiner Fehler!");
                return true;
            }
            sender.sendMessage("§b==== §6BanInfo: " + args[0] + " §b====");
            sender.sendMessage("§bBann-ID: §6" + bi.id);
            sender.sendMessage("§bgebannt von: §6" + bi.bannedByName);
            sender.sendMessage("§bBannzeit: §6" + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(bi.banTimestamp * 1000/*convert back to millis*/)));
            sender.sendMessage("§bgültig bis: §6" + ((bi.banExpiryTimestamp < 0) ? "permanent" : (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(bi.banExpiryTimestamp * 1000/*convert back to millis*/))));
//			sender.sendMessage("§bgenerischer Grund: §6"+bi.genericReasonId);
            sender.sendMessage("§bGrund: §6" + bi.reason);
            return true;
        }
        sender.sendMessage("§6Verwendung: §b/baninfo <SPIELER>");
        return true;
    }

}
