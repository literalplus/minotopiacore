package io.github.xxyy.mtc.hook.vault;

import io.github.xxyy.mtc.hook.Hooks;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Simple implementation of a chat hook for Vault.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-04
 */
public class ChatHookImpl implements ChatHook {
    private Chat chat;

    @Override
    public String getPlayerPrefix(Player player) {
        return chat.getPlayerPrefix(player);
    }

    @Override
    public boolean isAvailable(Plugin plugin) {
        return chat != null;
    }

    @Override
    public void hook(Plugin plugin) throws Exception, NoClassDefFoundError {
        chat = Hooks.setupProvider(Chat.class, plugin);
    }
}
