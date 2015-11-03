package io.github.xxyy.mtc.hook.vault;

import io.github.xxyy.mtc.hook.Hooks;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

/**
 * A simple unsafe implementation of a hook for the Vault plugin API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public class EconomyHookImpl implements EconomyHook {
    private Unsafe unsafe;

    @Override
    public boolean isAvailable(Plugin plugin) {
        return hasUnsafe() && unsafe.economy != null;
    }

    @Override
    public void hook(Plugin plugin) {
        unsafe = new Unsafe(plugin);
    }

    @Override
    public boolean assureHasAccount(OfflinePlayer player) {
        return unsafe.economy.hasAccount(player) || unsafe.economy.createPlayerAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return unsafe.economy.getBalance(player);
    }

    @Override
    public EcoResponse depositPlayer(OfflinePlayer player, double amount) {
        return unsafe.wrap(unsafe.depositPlayer(player, amount));
    }

    @Override
    public EcoResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return unsafe.wrap(unsafe.withdrawPlayer(player, amount));
    }

    @Override
    public Unsafe getUnsafe() {
        return unsafe;
    }

    @Override
    public boolean hasUnsafe() {
        return unsafe != null;
    }

    public class Unsafe implements EconomyHook.Unsafe {
        protected Economy economy;

        private Unsafe(Plugin plugin) {
            economy = Hooks.setupProvider(Economy.class, plugin);
        }

        @Override
        public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
            return economy.depositPlayer(player, amount);
        }

        @Override
        public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
            return economy.withdrawPlayer(player, amount);
        }

        public EcoResponse wrap(EconomyResponse original) {
            return new EcoResponse(original.transactionSuccess(), original.errorMessage);
        }
    }
}
