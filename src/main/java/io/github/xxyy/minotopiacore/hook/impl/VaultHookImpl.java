package io.github.xxyy.minotopiacore.hook.impl;

import io.github.xxyy.minotopiacore.hook.VaultHook;
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
public class VaultHookImpl {

    private final VaultHook wrapper;
    private Chat chat;
    private Economy economy;
    private Permission permission;

    public VaultHookImpl(VaultHook wrapper) {
        this.wrapper = wrapper;

        chat = setupProvider(Chat.class);
        economy = setupProvider(Economy.class);
        permission = setupProvider(Permission.class);
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

    private <T> T setupProvider(Class<T> providerClass) {
        T provider = null;

        try {
            provider = wrapper.getPlugin().getServer().getServicesManager().getRegistration(providerClass).getProvider();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(provider == null) {
            wrapper.getPlugin().getLogger().warning("Failed to hook Vault "+providerClass.getSimpleName()+"!");
        }

        return provider;
    }

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
