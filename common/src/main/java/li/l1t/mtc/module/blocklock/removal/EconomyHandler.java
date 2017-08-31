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
            throw new UserException("Das Entfernen von Blöcken dieser Art kostet %.0f MineCoins. " +
                    "Das kannst du dir leider nicht leisten.", removalCost);
        }
        MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                "%.0f MineCoins wurden von deinem Konto abgebucht.", removalCost);
        return true;
    }

    @Override
    public void describeTo(CommandSender sender) {
        MessageType.RESULT_LINE.sendTo(sender, "Das Entfernen dieses Blocks kostet %.0f MineCoins.", removalCost);
    }
}
