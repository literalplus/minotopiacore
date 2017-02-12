/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.listener;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.BlockLockModule;
import li.l1t.mtc.module.blocklock.service.BlockLockService;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for block interact events related to the BlockLockTool
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-10
 */
public class BlockLockToolListener implements Listener {
    private final BlockLockService lockService;
    private final MTCPlugin plugin;

    @InjectMe
    public BlockLockToolListener(BlockLockService lockService, MTCPlugin plugin) {
        this.lockService = lockService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isNotABlockLockTool(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        Block placedBlock = event.getClickedBlock().getRelative(event.getBlockFace());
        plugin.async(() -> lockService.sendLockStatusTo(placedBlock, event.getPlayer()));
    }

    private boolean isNotABlockLockTool(ItemStack item) {
        return item == null || item.getType() != BlockLockModule.TOOL_TYPE ||
                !item.getItemMeta().hasDisplayName() ||
                !item.getItemMeta().getDisplayName().equals(BlockLockModule.TOOL_DISPLAY_NAME);
    }
}
