/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.command;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.LocationHelper;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.blocklock.BlockLockModule;
import li.l1t.mtc.module.blocklock.service.BlockLockService;

/**
 * Executes the /bl command, which provides a few utilities for the BlockLock module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class BlockLockCommand extends MTCExecutionExecutor {
    private final BlockLockService lockService;

    @InjectMe
    public BlockLockCommand(BlockLockService lockService) {
        this.lockService = lockService;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (!exec.hasNoArgs()) {
            switch (exec.arg(0).toLowerCase()) {
                case "destroy":
                    XyLocation location = LocationHelper.deserialize(exec.arg(1));
                    lockService.destroyLockAndRefund(location.getBlock(), exec.player());
                    return true;
                case "tool":
                    exec.requirePermission(BlockLockModule.INFO_PERMISSION);
                    exec.player().getInventory().addItem(
                            new ItemStackFactory(BlockLockModule.TOOL_TYPE)
                                    .displayName(BlockLockModule.TOOL_DISPLAY_NAME)
                                    .produce()
                    );
                    exec.respond(MessageType.RESULT_LINE_SUCCESS,
                            "Mit diesem Block kannst du inspizieren, wo ein geschützter Block war.");
                    return true;
            }
        }
        exec.respondUsage("tool", "", "Gibt dir das Inspeaktionstool.");
        return true;
    }
}
