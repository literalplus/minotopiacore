/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.stack;

import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.misc.cmd.MTCPlayerOnlyCommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

/**
 * Provides a command that offers more fine-grained control over who may stack what and how many
 * using permissions.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-28
 */
class StackCommand extends MTCPlayerOnlyCommandExecutor {
    private final StackModule module;

    StackCommand(StackModule module) {
        this.module = module;
    }

    @Override
    public boolean catchCommand(Player player, String plrName, Command cmd, String label, String[] args) {
        int maxOversizedStackSize = findAllowedOversizedStackSizeFor(player);
        Predicate<ItemStack> stackablePredicate = module::isStackingPermitted;
        if (!player.hasPermission("mtc.stack.all")) {
            stackablePredicate.and(module::isCoveredByAllowedSpecs);
        }
        InventoryCompactor compactor = new InventoryCompactor(maxOversizedStackSize, stackablePredicate);
        compactor.compact(player.getInventory());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Dein Inventar wurde gestackt!");
        return true;
    }

    private Integer findAllowedOversizedStackSizeFor(CommandSender sender) {
        return module.getOverrideSizesOrderedInReverse().stream()
                .filter(size -> sender.hasPermission("mtc.stack.oversized." + size))
                .findFirst().orElse(1);
    }
}
