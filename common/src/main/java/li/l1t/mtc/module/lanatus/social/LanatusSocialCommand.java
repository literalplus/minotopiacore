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

package li.l1t.mtc.module.lanatus.social;

import li.l1t.common.command.BukkitExecution;
import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.UserException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.command.MTCExecutionExecutor;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.util.UUID;

/**
 * Executes the /lashare command, which allows players to share purchases.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class LanatusSocialCommand extends MTCExecutionExecutor {
    private final LanatusSocialModule module;
    private final LanatusClient lanatus;

    @InjectMe
    public LanatusSocialCommand(LanatusSocialModule module, MTCLanatusClient lanatus) {
        this.module = module;
        this.lanatus = lanatus;
    }

    @Override
    public boolean execute(BukkitExecution exec) throws UserException, InternalException {
        if (exec.hasArg(0)) {
            switch (exec.arg(0).toLowerCase()) {
                case "preview":
                    return handlePreview(exec, exec.uuidArg(1));
                case "share":
                    return handleShare(exec, exec.uuidArg(1));
            }
        }
        return respondUsage(exec);
    }

    private boolean handleShare(BukkitExecution exec, UUID purchaseId) {
        checkIsShareable(purchaseId);
        Purchase purchase = lanatus.purchases().findById(purchaseId);
        module.getPlugin().getServer().broadcastMessage(formatShareMessage(exec, purchase));
        module.markShared(purchase);
        return true;
    }

    private boolean handlePreview(BukkitExecution exec, UUID purchaseId) {
        checkIsShareable(purchaseId);
        Purchase purchase = lanatus.purchases().findById(purchaseId);
        exec.respond(MessageType.RESULT_LINE, "So würde die Nachricht aussehen:");
        exec.respond(formatShareMessage(exec, purchase));
        return true;
    }

    private String formatShareMessage(BukkitExecution exec, Purchase purchase) {
        return module.getShareMessage()
                .replaceAll("\\$player", exec.senderName())
                .replaceAll("\\$product", purchase.getProduct().getDisplayName());
    }

    private void checkIsShareable(UUID purchaseId) {
        if (!module.isShareable(purchaseId)) {
            throw new UserException("Dieser Kauf kann nicht mehr geteilt werden, da er schon zu lange her ist.");
        }
    }

    private boolean respondUsage(BukkitExecution exec) {
        exec.respondUsage("preview", "<Kauf-ID>", "Zeigt Vorschau der Nachricht");
        exec.respondUsage("share", "<Kauf-ID>", "Sendet Nachricht");
        return true;
    }
}
