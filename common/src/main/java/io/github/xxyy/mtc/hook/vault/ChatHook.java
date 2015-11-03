package io.github.xxyy.mtc.hook.vault;

import io.github.xxyy.mtc.hook.MTCHook;
import org.bukkit.entity.Player;

/**
 * Declares the interface contract for a Vault chat hook. This hook provides an interface for the Vault API.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-03
 */
public interface ChatHook extends MTCHook {
    String getPlayerPrefix(Player player);
}
