/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.listener;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.service.BlockLockService;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens for interactions on locked blocks and blocks them.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class BlockLockInteractionListener implements Listener {
    private final BlockLockService lockService;
    private final MTCPlugin plugin;

    @InjectMe
    public BlockLockInteractionListener(BlockLockService lockService, MTCPlugin plugin) {
        this.lockService = lockService;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (isIrrelevant(event)) {
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> lockService.sendLockStatusTo(event.getClickedBlock(), event.getPlayer())
        );
    }

    private boolean isIrrelevant(PlayerInteractEvent event) {
        return !isRelevantClick(event) ||
                event.getClickedBlock() == null ||
                !lockService.isLockable(event.getClickedBlock());
    }

    private boolean isRelevantClick(PlayerInteractEvent event) {
        return event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().getGameMode() == GameMode.CREATIVE);
    }
}
