/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.stack;

import li.l1t.common.exception.UserException;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        switch (args[0].toLowerCase()) {
            case "allow":
                handleAllow(sender, args);
                break;
            case "deny":
                handleDeny(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "describe":
                handleDescribe(sender);
                break;
            default:
                MessageType.USER_ERROR.sendTo(sender, "Unbekannter Befehl: /stacka %s", args[0]);
        }
        return true;
    }

    private void handleList(CommandSender sender) {
        MessageType.LIST_HEADER.sendTo(sender, "Erlaubte Items:");
        module.getAllowedSpecs().forEach(spec -> MessageType.LIST_ITEM.sendTo(sender, spec.toString()));
        MessageType.RESULT_LINE.sendTo(sender, "Anzahl: %s", module.getAllowedSpecs().size());
    }

    private void handleAllow(CommandSender sender, String[] args) {
        ItemSpec spec = parseItemSpec(args[1]);
        if (module.addAllowedSpecIfNotPresent(spec)) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Item erlaubt: %s", spec);
        } else {
            MessageType.USER_ERROR.sendTo(sender, "Item ist bereits erlaubt: %s", spec);
        }
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

    private void handleDeny(CommandSender sender, String[] args) {
        ItemSpec spec = parseItemSpec(args[1]);
        if (module.removeAllowedSpecIfPresent(spec)) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(sender, "Item ist nicht mehr erlaubt: %s", spec);
        } else {
            MessageType.USER_ERROR.sendTo(sender, "Item ist nicht erlaubt: %s", spec);
        }
    }

    private void handleDescribe(CommandSender sender) {
        if (CommandHelper.kickConsoleFromMethod(sender, "stacka")) {
            return;
        }
        ItemStack itemInHand = ((Player) sender).getItemInHand();
        if (itemInHand == null) {
            MessageType.USER_ERROR.sendTo(sender, "Du hast nichts in der Hand.");
            return;
        }
        describeItemInHandTo(sender, itemInHand);
    }

    private void describeItemInHandTo(CommandSender sender, ItemStack itemInHand) {
        ItemSpec spec = new ItemSpec(itemInHand.getType(), itemInHand.getDurability());
        MessageType.RESULT_LINE.sendTo(sender, "Das Item in deiner Hand ist '%s'.", spec);
        describeAllowedState(sender, itemInHand);
    }

    private void describeAllowedState(CommandSender sender, ItemStack itemInHand) {
        int maxStackSize = itemInHand.getMaxStackSize();
        MessageType.RESULT_LINE.sendTo(sender, "Es ist standardmäßig auf %s stackbar.", maxStackSize);
        if (maxStackSize == 64) {
            return;
        }
        boolean currentlyAllowed = module.isCoveredByAllowedSpecs(itemInHand);
        String allowedString = currentlyAllowed ? "erlaubt" : "nicht erlaubt";
        MessageType.RESULT_LINE.sendTo(sender, "Höhere Stacksizes sind momentan %s.", allowedString);
    }

    private boolean sendHelpTo(CommandSender sender) {
        sender.sendMessage("§e/stacka allow [Material<:Datenwert>] - Erlaubt ein Item");
        sender.sendMessage("§e/stacka deny [Material<:Datenwert>] - Verbietet ein Item");
        sender.sendMessage("§e/stacka list allowed - Listet erlaubte Items auf");
        sender.sendMessage("§e/stacka describe hand - Erzählt dir was über das Item in deiner Hand");
        sender.sendMessage("§6Grundsätzlich dürfen Leute mit mtc.stack nur erlaubte Items stacken, " +
                "es sei denn sie haben mtc.stack.all. Ein Item zu verbieten entfernt es nur aus " +
                "der Liste der erlaubten Items. Die maximale Stackgröße wird über die Permission " +
                "mtc.stack.oversized.<Zahl> definiert. Welche Zahlen überprüft werden, kann in " +
                "der Config eingestellt werden.");
        return true;
    }
}
