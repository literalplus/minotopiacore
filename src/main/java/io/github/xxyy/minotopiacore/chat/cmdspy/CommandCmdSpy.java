package io.github.xxyy.minotopiacore.chat.cmdspy;

import io.github.xxyy.common.util.CommandHelper;
import io.github.xxyy.common.util.StringHelper;
import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandCmdSpy implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmdspy", label)) {
            return true;
        }
        if (CommandHelper.kickConsoleFromMethod(sender, label)) {
            return true;
        }

        Player plr = (Player) sender;

        if (args.length > 0 && !args[0].equalsIgnoreCase("help")) {
            if (args.length > 1) { //If we have enough args, check sub-commands that require an argument
                switch (args[0].toLowerCase()) {
                    case "-i": //Target is a command
                        sender.sendMessage(MTC.chatPrefix + "CommandSpy -i " +
                                enabledString(
                                        CommandSpyFilters.toggleStringFilter(
                                                StringHelper.varArgsString(args, 1, false), plr
                                        )) +
                                "!");
                        return true;
                    case "-p": //Target is a player
                        @SuppressWarnings("deprecation")
                        Player target = Bukkit.getPlayerExact(args[1]); //Intended!
                        if (target == null) {
                            sender.sendMessage(MTC.chatPrefix + "Sorry, dieser Spieler ist nicht online :/");
                        } else {
                            sender.sendMessage(MTC.chatPrefix + "CommandSpy -p " +
                                    enabledString(
                                            CommandSpyFilters.togglePlayerFilter(
                                                    target.getUniqueId(), plr
                                            )) +
                                    "!");
                        }
                        return true;
                } //So we haven't matched any of the sub-commands requiring arguments.
            }
            //Well, we still have those without!
            switch (args[0].toLowerCase()) {
                case "-c": //Clear all filters (that can be cleared)
                    CommandSpyFilters.unsubscribeFromAll(plr.getUniqueId());
                    sender.sendMessage(MTC.chatPrefix + "Alle Filter deaktiviert!");
                    return true;
                case "-l": //List filters
                    int filterAmount = CommandSpyFilters.getSubscribedFilters(plr.getUniqueId())
                            .mapToInt(filter -> {
                                plr.sendMessage(filter.niceRepresentation()); //This is where they get the info about the filter
                                return 1;
                            }).sum();

                    sender.sendMessage(MTC.chatPrefix + "Du hast " + (filterAmount == 0 ? "keine" : filterAmount) + " Filter abonniert.");
                    return true;
                case "-a": //Target all commands
                    sender.sendMessage(MTC.chatPrefix + "CommandSpy -a " +
                            enabledString(
                                    CommandSpyFilters.toggleGlobalFilter(
                                            plr
                                    )) +
                            "!");
                    return true;
            }
        }

        return this.printHelpTo(sender);
    }

    public String enabledString(boolean enabled) {
        return (enabled ? "" : "de") + "aktiviert";
    }

    public boolean printHelpTo(CommandSender sender) {
        sender.sendMessage("§e/cmdspy -a §6Aktiviert CommandSpy für alle Befehle.");
        sender.sendMessage("§e/cmdspy -i <CMD> §6Aktiviert CommandSpy für einen einzelnen Befehl.");
        sender.sendMessage("§e/cmdspy -p <SPIELER> §6Aktiviert CommandSpy für einen bestimmten Spieler.");
        sender.sendMessage("§e/cmdspy -c §6Deaktiviert CommandSpy");
        sender.sendMessage("§e/cmdspy -l §6Zeigt alle aktivierten Filter an.");
        sender.sendMessage("§c-i akzeptiert (nur) Befehle ohne Slash. Beginne das Argument mit !r, um es als regulären Ausdruck zu verwenden.");
        return true;
    }
}
