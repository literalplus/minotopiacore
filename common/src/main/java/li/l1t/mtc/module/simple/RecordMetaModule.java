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

package li.l1t.mtc.module.simple;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.MTCModuleAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Removes item meta from all records in a player's inventory.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-07
 */
public class RecordMetaModule extends MTCModuleAdapter {
    public RecordMetaModule() {
        super("RecordMeta", false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        registerCommand(new RecordMetaCommand(), "recordmeta", "rm");
    }

    private static class RecordMetaCommand extends MTCExecutionExecutor {
        @Override
        public boolean execute(BukkitExecution exec) throws UserException, InternalException {
            if (exec.findArg(0).filter("help"::equalsIgnoreCase).isPresent()) {
                exec.respondUsage("", "",
                        "Entfernt Verzauberungen von allen Schallplatten in deinem Inventar, sodass du sie " +
                                "bei den Händlern des Schreckens verwenden kannst.");
                return true;
            }
            PlayerInventory inventory = exec.player().getInventory();
            List<ItemStack> resultsList = Arrays.stream(inventory.getStorageContents())
                    .map(this::removeMetaIfRecord)
                    .collect(Collectors.toList());
            inventory.setStorageContents(resultsList.toArray(new ItemStack[resultsList.size()]));
            exec.respond(MessageType.RESULT_LINE_SUCCESS,
                    "Von allen Schallplatten in deinem Inventar wurden die Verzauberungen entfernt. " +
                            "Du kannst sie jetzt bei den Händlern des Schreckens verwenden.");
            return true;
        }

        private ItemStack removeMetaIfRecord(ItemStack inputStack) {
            if (inputStack != null && inputStack.getType().isRecord()) {
                inputStack.setItemMeta(null);
            }
            return inputStack;
        }
    }
}
