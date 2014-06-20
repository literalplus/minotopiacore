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
        if (!CommandHelper.checkPermAndMsg(sender, "mtc.cmdspy", label)) return true;
        if (CommandHelper.kickConsoleFromMethod(sender, label)) return true;

        Player plr = (Player) sender;

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            return this.printHelpTo(sender);
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("-i")) {
            sender.sendMessage(MTC.chatPrefix + "CommandSpy -i " +
                    enabledString(CommandSpyFilters.toggleStringFilter(StringHelper.varArgsString(args, 1, false), plr)) +
                    "!");
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("-p")) {
            @SuppressWarnings("deprecation")
            Player target = Bukkit.getPlayerExact(args[1]); //Intended!
            if (target == null) {
                sender.sendMessage(MTC.chatPrefix + "Sorry, dieser Spieler ist nicht online :/");
            } else {
                sender.sendMessage(MTC.chatPrefix + "CommandSpy -p " +
                        enabledString(CommandSpyFilters.togglePlayerFilter(target.getUniqueId(), plr)) +
                        "!");
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("-l")) {
            int filterAmount = CommandSpyFilters.getSubscribedFilters(plr.getUniqueId())
                    .mapToInt(filter -> {
                        plr.sendMessage(filter.niceRepresentation());
                        return 1;
                    }).sum();

            sender.sendMessage(MTC.chatPrefix + "Du hast " + filterAmount + " Filter abonniert.");
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("-c")) {
            CommandSpyFilters.unsubscribeFromAll(plr.getUniqueId());
            sender.sendMessage(MTC.chatPrefix + "Alle Filter deaktiviert!");
        } else if (args.length == 0) {
            if (!CommandSpyFilters.ALL_FILTER.getSubscribers().remove(plr.getUniqueId())) {
                CommandSpyFilters.ALL_FILTER.getSubscribers().add(plr.getUniqueId());
                sender.sendMessage(MTC.chatPrefix + "CommandSpy aktiviert!");
            } else {
                sender.sendMessage(MTC.chatPrefix + "CommandSpy deaktiviert!");
            }
        } else return this.printHelpTo(sender);

        return true;
    }

    public String enabledString(boolean enabled) {
        return (enabled ? "" : "de") + "aktiviert";
    }

    public boolean printHelpTo(CommandSender sender) {
        sender.sendMessage("§e/cmdspy §6Aktiviert CommandSpy.");
        sender.sendMessage("§e/cmdspy -i <CMD> §6Aktiviert CommandSpy für einen einzelnen Befehl.");
        sender.sendMessage("§e/cmdspy -p <SPIELER> §6Aktiviert CommandSpy für einen bestimmten Spieler.");
        sender.sendMessage("§e/cmdspy -c §6Deaktiviert CommandSpy");
        sender.sendMessage("§e/cmdspy -l §6Zeigt alle aktivierten Filter an.");
        sender.sendMessage("§c-i akzeptiert Befehle ohne Slash. Beginne das Argument mit !r, um es als regulären Ausdruck zu verwenden.");
        return true;
    }
}
