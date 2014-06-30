package io.github.xxyy.minotopiacore.bans.cmd;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import io.github.xxyy.minotopiacore.bans.BanHelper;
import io.github.xxyy.minotopiacore.bans.BanInfo;
import io.github.xxyy.minotopiacore.misc.BungeeHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;


public final class CommandBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.ban.permanent", label)) {
            return true;
        }
        if (args.length >= 2 && !args[0].equalsIgnoreCase("help")) {
            Player plr = Bukkit.getPlayerExact(args[0]);
            if (plr == null) {
                OfflinePlayer oPlr = Bukkit.getOfflinePlayer(args[0]);
                if (oPlr == null) {
                    sender.sendMessage(MTC.banChatPrefix + "Dieser Spieler ist dem System nicht bekannt.");
                } else {
                    sender.sendMessage(MTC.banChatPrefix + "Dieser Spieler ist nicht online.");
                }
            }
            String reason = args[1];
            if (args.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (byte i = 2; i < args.length; i++) {
                    sb.append(' ').append(args[i]);
                }
                reason += sb.toString();
            }
            reason = ChatColor.translateAlternateColorCodes('&', reason);
            reason += " " + MTC.instance().warnBanServerSuffix;
            BanHelper.setBanned(args[0].toLowerCase(), sender.getName(), reason, (byte) 0, -42L);//return won't work
            BanInfo bi = BanHelper.getBanInfoByPlayerName(args[0].toLowerCase()); //REFACTOR BanInfo should be returned by above method
            if (plr != null) {
                plr.kickPlayer(BanHelper.getBanReasonForKick(bi, true));
            }

            if (ConfigHelper.isEnableBungeeAPI()) {
                BungeeHelper.notifyServersBan(bi);
            } else {
                BanHelper.broadcastBanChatMsg(bi);
            }

            LogHelper.getBanLogger().log(Level.WARNING, sender.getName() + " BANNED: " + args[0] + " COZ: " + reason + " ID: " + bi.id);
            return true;
        }
        sender.sendMessage("§6Verwendung: §b/ban <SPIELER> <GRUND>");
        return true;
    }

}
