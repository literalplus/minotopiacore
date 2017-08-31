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

package li.l1t.mtc.hook.impl;

import li.l1t.mtc.hook.HookWrapper;
import li.l1t.mtc.hook.Hooks;
import li.l1t.mtc.hook.VaultHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

/**
 * Implements unsafe parts of the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class VaultHookImpl implements Hook {
    private Chat chat;
    private Economy economy;
    private Permission permission;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return wrapper instanceof VaultHook && wrapper.getPlugin().getServer().getPluginManager().getPlugin("Vault") != null;
    }

    @Override
    public void hook(HookWrapper wrapper) {
        chat = Hooks.setupProvider(Chat.class, wrapper.getPlugin());
        economy = Hooks.setupProvider(Economy.class, wrapper.getPlugin());
        permission = Hooks.setupProvider(Permission.class, wrapper.getPlugin());
    }

    @Override
    public boolean isHooked() {
        return true; //Can't really say
    }

    public boolean assureHasAccount(OfflinePlayer offlinePlayer) {
        return getEconomy().hasAccount(offlinePlayer) || getEconomy().createPlayerAccount(offlinePlayer);
    }

    public double getBalance(OfflinePlayer offlinePlayer) {
        return getEconomy().getBalance(offlinePlayer);
    }

    public String getPlayerPrefix(OfflinePlayer offlinePlayer) {
        return getChat().getPlayerPrefix(null, offlinePlayer);
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        return getEconomy().depositPlayer(offlinePlayer, amount);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return getEconomy().withdrawPlayer(offlinePlayer, amount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Chat getChat() {
        return chat;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermission() {
        return permission;
    }
}
