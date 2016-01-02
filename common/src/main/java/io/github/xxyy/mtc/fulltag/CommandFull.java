/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.fulltag;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.LogHelper;
import io.github.xxyy.mtc.helper.MTCHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class CommandFull implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!ConfigHelper.getFullTagAllowedPlayers().contains(sender.getName())) {
            sender.sendMessage("§4Thou shall not pass! §c(Du darfst diesen Befehl nicht benutzen)");
            LogHelper.getFullLogger().warning("Unauthorized CommandSender " + sender + " tried to use /full command!");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            CommandFull.printHelpTo(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("info")) {
            return CommandFull.interpretInfo(sender, args);
        } else if (args[0].equalsIgnoreCase("get")) {
            return CommandFull.interpretGet(sender, args);
        } else if (args[0].equalsIgnoreCase("stats")) {
            return CommandFull.interpretStats(sender, args);
        } else {
            sender.sendMessage("§cUnbekannte Aktion! Hilfe:");
            CommandFull.printHelpTo(sender);
        }
        return true;
    }

    private static boolean interpretGet(CommandSender sender, String[] args) {
        if (args.length < 4 || !args[1].startsWith("-")) {//If not -, we don't know what to do
            sender.sendMessage("§cUnbekannte Verwendung von /full get [..]!");
            CommandFull.printHelpTo(sender);
            return true;
        }
        byte startIndex = 1;
        boolean thorns = false;
        boolean getAll = false;
        byte partId = 0;
        for (byte i = 1; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                break;
            }
            if (args[i].equalsIgnoreCase("-t")) {
                thorns = true;
                startIndex++;
                continue;
            }
            if (args[i].equalsIgnoreCase("-a")) {
                getAll = true;
                startIndex++;
                continue;
            }
            if (args[i].equalsIgnoreCase("-p")) {
                if (args.length <= i + 2) {
                    sender.sendMessage("§cUnbekannte Verwendung von /full get -p [..]!");
                    CommandFull.printHelpTo(sender);
                    return true;
                }
                try {
                    partId = Byte.parseByte(args[i + 1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cUnbekannte Verwendung von /full get -p <ZAHL> [..]: Argument ist nicht numerisch!");
                    CommandFull.printHelpTo(sender);
                    return true;
                }
                i++;//We used up an extra arg ;)
                startIndex += 2;
                continue;
            }
            sender.sendMessage("§eNotiz: Unbekanntes Argument '" + args[i] + "'");
        }
        String recName = args[startIndex];
        String comment = "";
        Player receiver = Bukkit.getPlayerExact(recName);
        if (receiver == null) {
            sender.sendMessage("§cDieser Spieler ist nicht online! §eBeschwer' dich bei Bukkit.");
            return false;
        }
        if (args.length >= startIndex + 1) {
            for (byte i = (byte) (startIndex + 1); i < args.length; i++) {
                comment += ((i == startIndex + 1) ? "" : " ") + args[i];
            }
        }
        if (getAll) {
            for (byte i = 0; i <= 4/*sword*/; i++) {
                sender.sendMessage("§eGebe " + FullInfo.getPartNameById(i) + "...");
                FullTagHelper.tryGiveFull(thorns, i, false, sender, recName, comment, receiver.getInventory());
            }
        } else {
            sender.sendMessage("§eGebe " + FullInfo.getPartNameById(partId) + "...");
            FullTagHelper.tryGiveFull(thorns, partId, false, sender, recName, comment, receiver.getInventory());
        }
        return true;
    }

    private static boolean interpretInfo(CommandSender sender, String[] args) {
        FullInfo fi;
        if (args.length == 1) {
            if (CommandHelper.kickConsoleFromMethod(sender, "full info (hand")) {
                return true;
            }
            Player plr = (Player) sender;
            ItemStack is = plr.getItemInHand();
            int id = (is == null || is.getType() == Material.AIR) ? -12 : FullTagHelper.getFullId(is);
            if (id < 0) {
                plr.sendMessage("§cDu hast keine Full in der Hand!");
                return true;
            }
            fi = FullInfo.getById(id);
            if (fi.id < 0) {
                plr.sendMessage("§cDatenbankfehler: " + fi.id);
                return true;
            }
        } else {
            if (!StringUtils.isNumeric(args[1])) //                sender.sendMessage("§cDas ist keine Zahl!");
            {
                return CommandFull.interpretUserInfo(sender, args);
            }
            int id = Integer.parseInt(args[1]);
            fi = FullInfo.getById(id);
            if (fi.id < 0) {
                sender.sendMessage("§cDatenbankfehler: " + fi.id);
                return true;
            }
        }
        sender.sendMessage("§9---- §2Fullinfo: " + fi.id + " §9----");
        sender.sendMessage("§9gegeben von: §2" + fi.senderName);
        sender.sendMessage("§9gegeben für: §2" + fi.receiverName);
        sender.sendMessage("§9gegeben um: §2" + (new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(fi.timestamp * 1000)));
        sender.sendMessage("§9Kommentar: §2" + fi.comment);
        sender.sendMessage("§9Thorns? §2" + fi.thorns);
        sender.sendMessage("§9Teil: §2" + FullInfo.getPartNameById(fi.partId));
        sender.sendMessage("§9Zuletzt gesehen:");
        sender.sendMessage("  §9bei: §2x=" + fi.x + ",y=" + fi.y + ",z=" + fi.z);
        sender.sendMessage("  §9Spieler: §2" + fi.lastOwnerName);
        sender.sendMessage("  §9um: §2" + (new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(fi.lastseen * 1000)));
        sender.sendMessage("  §9Aktion: §2" + fi.lastCode);
        sender.sendMessage("  §9Enderkiste? §2" + fi.inEnderchest);
        return true;
    }

    private static boolean interpretStats(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandFull.printStatsHelp(sender);
        }
        switch (args[1]) {
            case "owner":
                return CommandFull.interpretStatsOwner(sender, args);
            case "sender":
                return CommandFull.interpretStatsSender(sender, args);
            case "thorns":
                return CommandFull.interpretStatsThorns(sender);
            case "ender":
                return CommandFull.interpretStatsEnder(sender);
            case "part":
                return CommandFull.interpretStatsPart(sender);
            case "seen":
                return false;
            default:
                return CommandFull.printStatsHelp(sender);
        }
    }

    private static boolean interpretStatsEnder(CommandSender sender) {
        List<Integer> lst = FullTagHelper.getBooleanValCounts("enderchest");
        if (lst.size() == 1) {
            sender.sendMessage("§6Es gibt nur einen Wert (d.h. diese Anzeige funktioniert nicht)");
            return true;
        }
        int inside = lst.get(0);
        int outside = lst.get(1);
        int sum = inside + outside;
        sender.sendMessage("§9---- §6Enderkistenanalyse §9----");
        sender.sendMessage("§ein einer Enderkiste: §6" + inside + "(" + ((inside * 100) / sum) + "%)");
        sender.sendMessage("§eirgendwo anders: §6" + outside + "(" + ((outside * 100) / sum) + "%)");
        return true;
    }

    private static boolean interpretStatsOwner(CommandSender sender, String[] args) {
        int startIndex = 0;
        if (args.length >= 3) {
            if (!StringUtils.isNumeric(args[2])) {
                sender.sendMessage("§cDas ist keine Zahl!");
                return CommandFull.printStatsHelp(sender);
            }
            startIndex = (Integer.parseInt(args[2]) - 1) * 15;
        }
        Map<String, Integer> map = FullTagHelper.getTopFullOwners(startIndex);
        if (map == null || map.isEmpty()) {
            sender.sendMessage("§eLeeres Ergebnis -> entweder SQL-Fehler oder keine Fulls registriert.");
            return true;
        }
        sender.sendMessage("§9---- §6Top 15 Fullbesitzer (" + ((args.length >= 3) ? args[2] : 1) + ")");
        int i = 0;
        for (String name : map.keySet()) {
            sender.sendMessage("§e#" + ((i + 1) + startIndex) + " §9" + name + "§e hat §9" + map.get(name) + " §eFullteile.");
            i++;
        }
        return true;
    }

    private static boolean interpretStatsPart(CommandSender sender) {
        Map<Integer, Integer> map = FullTagHelper.getPartCounts();
        int sum = 0;
        if (map == null || map.isEmpty()) {
            sender.sendMessage("§e(SQL-)Fehler! <2");
            return true;
        }
        for (int i = -1; i < map.size(); i++) {
            if (i == -1) {
                sum = map.get(i);
                map.remove(-1);
                continue;
            }
            int val = (map.containsKey(i)) ? map.get(i) : 0;
            sender.sendMessage("§e" + FullInfo.getPartNameById(i) + "('" + i + "'): " + val + "\n §6" + MTCHelper.getProgressBar(38, val, sum));
        }
        sender.sendMessage("§eFullteile gesamt: " + sum);
        sender.sendMessage("§6[" + MTCHelper.getBarOfValues(54, new ArrayList<>(map.values()), sum) + "§6]");
        return true;
    }

    private static boolean interpretStatsSender(CommandSender sender, String[] args) {
        int startIndex = 0;
        if (args.length >= 3) {
            if (!StringUtils.isNumeric(args[2])) {
                sender.sendMessage("§cDas ist keine Zahl!");
                return CommandFull.printStatsHelp(sender);
            }
            startIndex = (Integer.parseInt(args[2]) - 1) * 15;
        }
        Map<String, Integer> map = FullTagHelper.getTopFullSenders(startIndex);
        if (map == null || map.isEmpty()) {
            sender.sendMessage("§eLeeres Ergebnis -> entweder SQL-Fehler oder keine Fulls registriert.");
            return true;
        }
        sender.sendMessage("§9---- §6Top 15 Fullsender (" + ((args.length >= 3) ? args[2] : 1) + ")");
        int i = 0;
        for (String name : map.keySet()) {
            sender.sendMessage("§e#" + ((i + 1) + startIndex) + " §9" + name + "§e hat §9" + map.get(name) + " §eFullteile versendet.");
            i++;
        }
        return true;
    }

    private static boolean interpretStatsThorns(CommandSender sender) {
        List<Integer> lst = FullTagHelper.getBooleanValCounts("thorns");
        if (lst.size() == 1) {
            sender.sendMessage("§6Es gibt nur einen Wert (d.h. diese Anzeige funktioniert nicht)");
            return true;
        }
        int with = lst.get(0);
        int without = lst.get(1);
        int sum = with + without;
        sender.sendMessage("§9---- §6Thrns-Analyse §9----");
        sender.sendMessage("§emit Thorns: §6" + with + "(" + ((with * 100) / sum) + "%)");
        sender.sendMessage("§eohne Thorns: §6" + without + "(" + ((without * 100) / sum) + "%)");
        return true;
    }

    private static boolean interpretUserInfo(CommandSender sender, String[] args) {
        Iterable<FullInfo> lstFor = FullTagHelper.getFullsWStringInRow(args[1], "`receiver_name`");
        List<FullInfo> lstCurrent = FullTagHelper.getFullsWStringInRow(args[1], "`lastowner`");
        sender.sendMessage("§9---- §6FullInfo: " + args[1] + " §9----");
        sender.sendMessage("§eFulls erhalten (IDs):\n§3" + MTCHelper.CSCollectionShort(lstFor));
        sender.sendMessage("§eFulls im Besitz (IDs):\n§3" + MTCHelper.CSCollectionShort(lstCurrent));
        return true;
    }

    private static void printHelpTo(CommandSender sender) {
        sender.sendMessage("§9/full - Fullmanagement 2.0");
        sender.sendMessage("§e/full info <ID> §6Zeigt Informationen zu einem Fullitem.");
        sender.sendMessage("§e/full info <Name> §6Zeigt Statistiken zu einem Spieler.");
        sender.sendMessage("§e/full info §6Zeigt Informationen zum Fullitem in deiner Hand");
        sender.sendMessage("§e/full get [-a|-t|-p <partId>] <Receiver> {Comment} §6Vergibt eine Full oder einen Fullteil..");
        sender.sendMessage("§e/full stats §6Statistiktool!");
        sender.sendMessage("§7-t §8Fügt Thorns hinzu.");
        sender.sendMessage("§7-a §8Wählt alle Rüstungsteile.");
        sender.sendMessage("§7-p <partId> §8Wählt nur einen Teil.");
        sender.sendMessage("§7partIds: 0=CHESTPLT;1=LEGS;2=BOOTS;3=HELMET;4=SWORD");
    }

    private static boolean printStatsHelp(CommandSender sender) {
        sender.sendMessage("§9---- §6Fullstats §9----");
        sender.sendMessage("§6/full stats owner [Seite] §eZeigt die 15 Leute an, die die meisten Fulls besitzen.");
        sender.sendMessage("§6/full stats sender [Seite] §eZeigt die 15 Leute an, die die meisten Fulls vergeben haben.");
        sender.sendMessage("§6/full stats thorns §eAnalysiert, wie viele Fulls THORNS haben.");
        sender.sendMessage("§6/full stats ender §eAnalysiert, wie viele Fulls in Enderkisten gelagert werden.");
        sender.sendMessage("§6/full stats part §eAnalysiert die Häufigkeit der einzelnen Fullteile.");
//        sender.sendMessage("§6/full stats seen §eAnalysiert, welche Fulls am längsten nicht mehr gesehen wurden.");
        return true;
    }
}
