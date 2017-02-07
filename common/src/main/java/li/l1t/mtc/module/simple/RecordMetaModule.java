/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
            if (inputStack.getType().isRecord()) {
                inputStack.setItemMeta(null);
            }
            return inputStack;
        }
    }
}
