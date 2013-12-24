/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.xxyy.minotopiacore.misc.cmd;

import io.github.xxyy.common.HelpManager;
import io.github.xxyy.common.cmd.XYCCommandExecutor;
import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.minotopiacore.MTC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Command that allows to manage the config file ingame.
 *
 * @author <a href="http://xxyy.github.io/">xxyy98</a>
 */
public final class CommandMTCConfig extends XYCCommandExecutor {

    @Override
    public boolean catchCommand(final CommandSender sender, final String senderName, final Command cmd, final String label, final String[] args) {
        if (args.length < 1) {
            return CommandHelper.msg("\u00a7Verwendung: /mtccfg help", sender);
        }

        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmd.mtc.config." + args[0], label + " " + args[0])) {
            return true;
        }

        switch (args[0]) {
        case "set":
            if (args.length < 3) {
                sender.sendMessage("§8Invalide Argumente für /" + label + " set. Hilfe:");
                HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                return true;
            }
            final String strValue = args[2];
            Object value = strValue;

            if (StringUtils.isNumeric(strValue)) {
                value = Integer.parseInt(strValue);
            } else {
                if (strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("false")) {
                    value = Boolean.parseBoolean(strValue);
                }
            }

            MTC.instance().getConfig().set(args[1], value);
            MTC.instance().saveConfig();
            sender.sendMessage("§7Konfiguartionswert §3" + args[2] + "§7 gesetzt auf: §3" + value + ".");
            CommandHelper.sendImportantActionMessage(sender, "Set Config Value §3" + args[2] + "§a§o to §3" + value);
            break;
        case "get":
            if (args.length < 2) {
                sender.sendMessage("§7Invalide Argumente für §3/" + label + " get§7. Hilfe:");
                HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
                return true;
            }
            final String fetchedValue = String.valueOf(MTC.instance().getConfig().get(args[2]));
            sender.sendMessage("§7Der Wert §3" + args[2] + "§7 ist im Moment gesetzt auf: §3" + fetchedValue + "§e.");
            break;
        case "reload":
            MTC.instance().reloadConfig();
            CommandHelper.sendImportantActionMessage(sender, "Reloaded mtc config");
            break;
        default:
            sender.sendMessage("§8Unbekannte Aktion " + args[1] + ". Hilfe:");
            HelpManager.tryPrintHelp("mtc", sender, label, "", "mtc help mtc");
        }
        return true;
    }

}
