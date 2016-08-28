/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.stack;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.exception.UserException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Provides admin commands to control allowed items for the stack module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-28
 */
class StackAdminCommand implements CommandExecutor {
    private final StackModule module;

    StackAdminCommand(StackModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
            return sendHelpTo(sender);
        }
        ItemSpec spec = parseItemSpec(args[1]);
        switch (args[0].toLowerCase()) {
            case "allow":
                handleAllow(sender, spec);
                break;
            case "deny":
                handleDeny(sender, spec);
                break;
            default:
                MessageType.USER_ERROR.sendTo(sender, "Unbekannter Befehl: /stacka %s", args[0]);
        }
        return true;
    }

    private ItemSpec parseItemSpec(String spec) {
        try {
            return ItemSpec.fromString(spec);
        } catch (NumberFormatException nfe) {
            throw UserException.wrapNotANumber(nfe);
        } catch (IllegalArgumentException e) {
            throw new UserException("Kann Itemspec '%s' nicht lesen: %s", spec, e.getMessage());
        }
    }

    private void handleAllow(CommandSender sender, ItemSpec spec) {
        if (module.addAllowedSpecIfNotPresent(spec)) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Item erlaubt: %s", spec);
        } else {
            MessageType.USER_ERROR.sendTo(sender, "Item ist bereits erlaubt: %s", spec);
        }
    }

    private void handleDeny(CommandSender sender, ItemSpec spec) {
        if (module.removeAllowedSpecIfPresent(spec)) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Item ist nicht mehr erlaubt: %s", spec);
        } else {
            MessageType.USER_ERROR.sendTo(sender, "Item ist nicht erlaubt: %s", spec);
        }
    }

    private boolean sendHelpTo(CommandSender sender) {
        sender.sendMessage("§e/stacka allow [Material<:Datenwert>] - Erlaubt ein Item");
        sender.sendMessage("§e/stacka deny [Material<:Datenwert>] - Verbietet ein Item");
        sender.sendMessage("§eGrundsätzlich dürfen Leute mit mtc.stack nur erlaubte Items stacken, " +
                "es sei denn sie haben mtc.stack.all. Ein Item zu verbieten entfernt es nur aus " +
                "der Liste der erlaubten Items. Die maximale Stackgröße wird über die Permission " +
                "mtc.stack.oversized.<Zahl> definiert. Welche Zahlen überprüft werden, kann in " +
                "der Config eingestellt werden.");
        return true;
    }
}
