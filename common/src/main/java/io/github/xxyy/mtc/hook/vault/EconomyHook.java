package io.github.xxyy.mtc.hook.vault;

import io.github.xxyy.mtc.hook.MTCHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

/**
 * Declares the interface contract for a Vault economy hook. This hook provides an interface for the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public interface EconomyHook extends MTCHook {
    boolean assureHasAccount(OfflinePlayer player);

    double getBalance(OfflinePlayer player);

    EcoResponse depositPlayer(OfflinePlayer player, double amount);

    EcoResponse withdrawPlayer(OfflinePlayer player, double amount);

    Unsafe getUnsafe();

    boolean hasUnsafe();

    /**
     * Defines unsafe parts of the hook API which expose backend classes which might not be loaded to the client.
     */
    interface Unsafe {
        EconomyResponse depositPlayer(OfflinePlayer player, double amount);

        EconomyResponse withdrawPlayer(OfflinePlayer player, double amount);
    }
}
