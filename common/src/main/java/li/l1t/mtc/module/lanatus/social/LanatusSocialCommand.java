/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.social;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.command.CommandExecution;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.BukkitExecutionExecutor;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.util.UUID;

/**
 * Executes the /lashare command, which allows players to share purchases.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class LanatusSocialCommand extends BukkitExecutionExecutor {
    private final LanatusSocialModule module;
    private final LanatusClient lanatus;

    @InjectMe
    public LanatusSocialCommand(LanatusSocialModule module, MTCLanatusClient lanatus) {
        this.module = module;
        this.lanatus = lanatus;
    }

    @Override
    public boolean execute(CommandExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0).toLowerCase()) {
                case "preview":
                    return handlePreview(exec, exec.uuidArg(0));
                case "share":
                    return handleSend(exec, exec.uuidArg(0));
            }
        }
        return respondUsage(exec);
    }

    private boolean handleSend(CommandExecution exec, UUID purchaseId) {
        checkIsShareable(purchaseId);
        Purchase purchase = lanatus.purchases().findById(purchaseId);
        exec.respond(MessageType.RESULT_LINE, "So würde die Nachricht aussehen:");
        exec.respond(formatShareMessage(exec, purchase));
        return true;
    }

    private boolean handlePreview(CommandExecution exec, UUID purchaseId) {
        checkIsShareable(purchaseId);
        Purchase purchase = lanatus.purchases().findById(purchaseId);
        module.getPlugin().getServer().broadcastMessage(formatShareMessage(exec, purchase));
        module.markShared(purchase);
        return true;
    }

    private String formatShareMessage(CommandExecution exec, Purchase purchase) {
        return module.getShareMessage()
                .replaceAll("$player", exec.senderName())
                .replaceAll("$product", purchase.getProduct().getDisplayName());
    }

    private void checkIsShareable(UUID purchaseId) {
        if (!module.isShareable(purchaseId)) {
            throw new UserException("Dieser Kauf kann nicht mehr geteilt werden, da er schon zu lange her ist.");
        }
    }

    private boolean respondUsage(CommandExecution exec) {
        exec.respondUsage("preview", "<Kauf-ID>", "Zeigt Vorschau der Nachricht");
        exec.respondUsage("share", "<Kauf-ID>", "Sendet Nachricht");
        return true;
    }
}
