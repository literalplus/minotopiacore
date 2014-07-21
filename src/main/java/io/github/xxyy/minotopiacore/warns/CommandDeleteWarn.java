package io.github.xxyy.minotopiacore.warns;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.LogHelper;
import io.github.xxyy.minotopiacore.MTC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Level;


public final class CommandDeleteWarn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.warns.remove", label)) {
            return true;
        }
        if (args.length <= 0 || args[0].equalsIgnoreCase("help")) {
            this.printHelpTo(sender);
            return true;
        }
        String targetName = args[0];
        boolean forceRemove = false;
        boolean unknownReason = false;
        boolean resetFlag = false;
        int removeCount = -127;
        byte ctr = 0; //'i' is overused in this class already :P
        for (String arg : args) { //REFACTOR
            if (arg.startsWith("-")) {
                if (arg.equalsIgnoreCase("-f")) {
                    if (!sender.hasPermission("mtc.warns.remove.force")) {
                        sender.sendMessage(MTC.warnChatPrefix + "§cDu darfst Warnungen nicht permanent löschen!");
                        return true;
                    }
                    forceRemove = true;
                    unknownReason = false;
                    ctr++;
                    continue;
                } else if (arg.equalsIgnoreCase("-r")) {
                    if (!sender.hasPermission("mtc.warns.remove.any")) {
                        sender.sendMessage(MTC.warnChatPrefix + "§cDu darfst keine Markierungen löschen!");
                        return true;
                    }
                    forceRemove = false;
                    unknownReason = false;
                    resetFlag = true;
                    ctr++;
                    continue;
                }
                if (arg.equalsIgnoreCase("-u")) {
                    if (!sender.hasPermission("mtc.warns.remove.any")) {
                        sender.sendMessage(MTC.warnChatPrefix + "§cDu darfst Warns nicht als unbekannt markieren!");
                        return true;
                    }
                    if (forceRemove) {
                        ctr++;
                        continue;
                    }//force remove overrides
                    unknownReason = true;
                    ctr++;
                    continue;
                } else if (arg.equalsIgnoreCase("-i")) {
                    if (args.length <= (ctr + 1)) {
                        sender.sendMessage(MTC.warnChatPrefix + "§cFehlendes Argument: §oEindeutige ID.");
                        this.printHelpTo(sender);
                        return true;
                    }
                    int uid;
                    try {
                        uid = Integer.parseInt(args[ctr + 1]);
                    } catch (NumberFormatException e) {
                        //oh well, let's do sth, shall we?
                        sender.sendMessage(MTC.warnChatPrefix + "§cInvalides Argument: §oEindeutige ID.");
                        this.printHelpTo(sender);
                        return true;
                    }
                    WarnInfo wi = WarnInfo.getById(uid);
                    if (wi.id < 0) {
                        sender.sendMessage(MTC.warnChatPrefix + "Fehler: " + wi.id);
                        return true;
                    }
                    if (resetFlag) {
                        wi.status = 0;
                        wi.flush();
                    } else if (forceRemove) {
                        wi.nullify();
                    } else if (unknownReason) {
                        wi.markUnknownReason();
                    } else {
                        wi.markInvalid();
                    }
                    sender.sendMessage(MTC.warnChatPrefix + "§aWarn §e" + uid + "§a erfolgrich entfernt.");
                    return true;
                } else if (arg.equalsIgnoreCase("-l")) {
                    if (!sender.hasPermission("mtc.warns.remove.own")) {
                        sender.sendMessage(MTC.warnChatPrefix + "§cDu darfst keine Warns löschen!");
                        return true;
                    }
                    WarnInfo wi = WarnInfo.getLastGivenByName(sender.getName());
                    if (wi.id < 0) {
                        sender.sendMessage(MTC.warnChatPrefix + "Fehler: " + wi.id);
                        return true;
                    }
                    if (forceRemove) {
                        wi.nullify();
                    } else if (unknownReason) {
                        wi.markUnknownReason();
                    } else {
                        wi.markInvalid();
                    }
                    sender.sendMessage(MTC.warnChatPrefix + "§aDein letzter Warn wurde erfolgreich entfernt:\n" + wi.toString());
                    return true;
                }
            }
            if (StringUtils.isNumeric(arg)) {
                try {
                    removeCount = Integer.parseInt(arg);
                } catch (NumberFormatException ignore) {
                    //lol...do nuthin' --> will be caught later by -127-check
                }
            }
            ctr++;
        }
        if (removeCount == -127) {
            sender.sendMessage(MTC.warnChatPrefix + "§cFehlendes/Invalides Argument: §oAnzahl der Warnungen");
            this.printHelpTo(sender);
            return true;
        }
        List<WarnInfo> warns = WarnHelper.getWarnsByPlayerName(targetName, false);
        if (warns.get(0).id == -4) {
            sender.sendMessage(MTC.warnChatPrefix + "§e" + args[0] + "§a hat keine Warnungen!");
            return true;
        }//there will always be an item in the list
        if (warns.get(0).id < 0) {
            sender.sendMessage(MTC.warnChatPrefix + "Fehler: " + warns.get(0).id);
            return true;
        }//there will always be an item in the list

        if (removeCount > warns.size()) {
            removeCount = warns.size();
        }

        if (forceRemove) {
            for (byte i = 0; i < removeCount; i++) {
                if (warns.size() <= i) {
                    break;
                }
                WarnInfo wi = warns.get(i);
                LogHelper.getWarnLogger().log(Level.WARNING, sender.getName() + " F-DW: " + targetName + " W-COZ: " + wi.reason + " BY: " + wi.warnedByName); //REFACTOR
                wi.nullify();
            }
        } else if (unknownReason) {
            for (byte i = 0; i < removeCount; i++) {
                if (warns.size() <= i) {
                    break;
                }
                WarnInfo wi = warns.get(i);
                wi.markUnknownReason();
                LogHelper.getWarnLogger().log(Level.INFO, sender.getName() + " F-DW: " + targetName + " W-COZ: " + wi.reason + " BY: " + wi.warnedByName + " ID: " + wi);
            }
        } else if (resetFlag) {
            for (byte i = 0; i < removeCount; i++) {
                if (warns.size() <= i) {
                    break;
                }
                WarnInfo wi = warns.get(i);
                wi.status = 0;
                wi.flush();
                LogHelper.getWarnLogger().log(Level.INFO, sender.getName() + " R-DW: " + targetName + " W-COZ: " + wi.reason + " BY: " + wi.warnedByName + " ID: " + wi);
            }
        } else {
            for (byte i = 0; i < removeCount; i++) {
                WarnInfo wi = warns.get(i);
                warns.get(i).markInvalid();
                LogHelper.getWarnLogger().log(Level.INFO, sender.getName() + " DW: " + targetName + " W-COZ: " + wi.reason + " BY: " + wi.warnedByName + " ID: " + wi);
            }
        }
        sender.sendMessage(MTC.warnChatPrefix + "§a Du hast §e" + args[0] + " §c" + removeCount + "§a Warnung" + ((removeCount == 1) ? "" : "en") + " entfernt.");
        return true;
    }

    public void printHelpTo(CommandSender sender) {
        sender.sendMessage("§cVerwendung: §6/delwarn <Spieler> [-f|-u|-r] [Anzahl]");
        sender.sendMessage("§cVerwendung: §6/delwarn [-f|-u|-r] -i [ID]");
        sender.sendMessage("§cVerwendung: §6/delwarn [-f|-u] -l");
        sender.sendMessage("§e-f: Löscht den Warn ENDGÜLTIG.");
        sender.sendMessage("§e-u: Markiert den Warn als 'unbekannter Grund'.");
        sender.sendMessage("§e-i: Wählt eine eindeutige ID (aka uid)");
        sender.sendMessage("§e-l: Löscht die letzte von dir vergebene Warnung");
        sender.sendMessage("§e-r: Entfernt sämtliche Markierungen (aka f|u).");
        sender.sendMessage("§3Arr-gumente sind order-sensitive! §7§o(like a pirate!)");
    }
}
