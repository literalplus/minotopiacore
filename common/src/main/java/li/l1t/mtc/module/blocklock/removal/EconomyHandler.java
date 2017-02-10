/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.removal;

import li.l1t.common.exception.UserException;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.hook.VaultHook;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Withdraws some money from the removing player's Vault account.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class EconomyHandler implements RemovalHandler {
    private final double removalCost;
    private final VaultHook vaultHook;

    public EconomyHandler(double removalCost, VaultHook vaultHook) {
        this.removalCost = removalCost;
        this.vaultHook = vaultHook;
    }

    @Override
    public boolean onRemove(BlockLock lock, Player player) {
        EconomyResponse response = vaultHook.withdrawPlayer(player, removalCost);
        if (!response.transactionSuccess()) {
            throw new UserException("Das Entfernen von Blöcken dieser Art kostet %d MineCoins. " +
                    "Das kannst du dir leider nicht leisten.", removalCost);
        }
        MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                "%d MineCoins wurden von deinem Konto abgebucht.", removalCost);
        return true;
    }

    @Override
    public void describeTo(CommandSender sender) {
        MessageType.RESULT_LINE.sendTo(sender, "Das Entfernen dieses Blocks kostet %d MineCoins.", removalCost);
    }
}
