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
        Predicate<ItemStack> oversizablePredicate = module::isStackingPermitted;
        if (!player.hasPermission("mtc.stack.all")) {
            oversizablePredicate = oversizablePredicate.and(module::isCoveredByAllowedSpecs);
        }
        InventoryCompactor compactor = new InventoryCompactor(maxOversizedStackSize, oversizablePredicate);
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
