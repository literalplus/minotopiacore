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

package li.l1t.mtc.hook;

import li.l1t.mtc.hook.impl.VaultHookImpl;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * Helps interfacing with Vault.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class VaultHook extends SimpleHookWrapper {
    private VaultHookImpl unsafe;

    public VaultHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    public boolean assureHasAccount(OfflinePlayer offlinePlayer) {
        return isEconomyHooked() && unsafe.assureHasAccount(offlinePlayer);
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        if (!isEconomyHooked()) {
            throw new IllegalStateException("Economy not hooked!");
        }

        return unsafe.getBalance(offlinePlayer);
    }

    public String getPlayerPrefix(OfflinePlayer offlinePlayer) {
        return isChatHooked() ? unsafe.getPlayerPrefix(offlinePlayer) : "§c[ERR]";
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) { //FIXME Will throw CNFE if no Vault loaded - shade?
        if (!isEconomyHooked()) {
            throw new IllegalStateException("Economy not hooked!");
        }

        return unsafe.depositPlayer(offlinePlayer, amount);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (!isEconomyHooked()) {
            throw new IllegalStateException("Economy not hooked!");
        }

        return unsafe.withdrawPlayer(offlinePlayer, amount);
    }

    /**
     * Checks with the Vault plugin whether given player can afford to pay a specified amount.
     *
     * @param offlinePlayer the player to check
     * @param amount        the amount of money the player must own for this method to return true
     * @return whether given player has given amount of money
     */
    public boolean canAfford(OfflinePlayer offlinePlayer, double amount) {
        return unsafe.getBalance(offlinePlayer) >= amount;
    }

    //// ACTIVITY METHODS //////////////////////////////////////////////////////////////////////////////////////////////

    public boolean arePermissionsHooked() {
        return isActive() && unsafe.getPermission() != null;
    }

    public boolean isChatHooked() {
        return isActive() && unsafe.getChat() != null;
    }

    public boolean isEconomyHooked() {
        return isActive() && unsafe.getEconomy() != null;
    }

    @Override
    public boolean isActive() {
        return unsafe != null;
    }
}
