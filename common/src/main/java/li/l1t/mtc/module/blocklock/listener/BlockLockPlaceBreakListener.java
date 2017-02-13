/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock.listener;

import li.l1t.common.exception.NonSensitiveException;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.NotLockedException;
import li.l1t.mtc.module.blocklock.service.BlockLockService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Creates locks when targeted materials are placed.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public class BlockLockPlaceBreakListener implements Listener {
    private final BlockLockService lockService;
    private final MTCPlugin plugin;

    @InjectMe
    public BlockLockPlaceBreakListener(BlockLockService lockService, MTCPlugin plugin) {
        this.lockService = lockService;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (lockService.isLockable(placedBlock)) {
            plugin.async(() -> {
                lockService.addLockTo(placedBlock, player);
                MessageType.RESULT_LINE_SUCCESS.sendTo(player,
                        "Dieser Block ist geschützt. Du kannst ihn später zerstören.");
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        /*
        Needs to be on HIGHEST to catch cancellations at lower priorities - we can't reliably catch cancellations
        of same-priority listeners though, because we need to cancel the event ourselves in order to perform the
        lock check async.
         */
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (lockService.isLockable(block)) {
            event.setCancelled(true);
            plugin.async(() -> {
                try {
                    lockService.destroyLockAndReturn(block, player);
                } catch (NotLockedException e) {
                    block.setType(Material.AIR, true);
                } catch (NonSensitiveException e) {
                    player.sendMessage(e.getColoredMessage());
                }
            });
        }
    }
}
