package io.github.xxyy.mtc.hook.impl;

import io.github.xxyy.mtc.hook.HookWrapper;
import io.github.xxyy.mtc.hook.Hooks;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;

import io.github.xxyy.mtc.hook.VaultHook;

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