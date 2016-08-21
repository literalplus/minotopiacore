/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.chat.handler;

import li.l1t.mtc.hook.VaultHook;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.chat.ChatModule;
import li.l1t.mtc.module.chat.api.ChatMessageEvent;
import li.l1t.mtc.module.chat.api.ChatPhase;
import li.l1t.mtc.module.chat.impl.ModuleAwareChatHandler;
import net.md_5.bungee.api.ChatColor;
import org.apache.logging.log4j.Logger;

/**
 * Adds Vault prefix and suffix to chat events.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-21
 */
public class VaultPrefixHandler extends ModuleAwareChatHandler {
    private static final Logger LOGGER = LogManager.getLogger(VaultPrefixHandler.class);

    public VaultPrefixHandler() {
        super(ChatPhase.INITIALISING);
    }

    @Override
    public boolean enable(ChatModule module) {
        super.enable(module);
        VaultHook vaultHook = getVaultHook();
        if (!vaultHook.isChatHooked()) {
            LOGGER.warn("Vault chat support is not loaded! Prefixes won't show in chat.");
            LOGGER.warn("If this is intended, disable VaultPrefixHandler in modules/chat.cfg.yml");
            return false;
        }
        return true;
    }

    @Override
    public void handle(ChatMessageEvent evt) {
        VaultHook vaultHook = getVaultHook();
        if (vaultHook.isChatHooked()) {
            evt.appendToPrefix(getVaultPrefix(evt, vaultHook));
        }
    }

    private String getVaultPrefix(ChatMessageEvent evt, VaultHook vaultHook) {
        String prefix = vaultHook.getPlayerPrefix(evt.getPlayer());
        return ChatColor.translateAlternateColorCodes('&', prefix) + " ";
    }

    private VaultHook getVaultHook() {
        return getModule().getPlugin().getVaultHook();
    }


}
