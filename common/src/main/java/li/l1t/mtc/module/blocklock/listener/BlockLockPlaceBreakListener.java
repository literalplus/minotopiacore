/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.blocklock.listener;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import li.l1t.mtc.module.blocklock.service.BlockLockService;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

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
        Doing computations async would make everything much more complicated, so we're trying how much of an performance
        impact this has for now.
         */
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Optional<BlockLock> lock = lockService.findLock(block);
        if (lock.isPresent()) {
            event.setCancelled(true);
            lockService.destroyLockAndRefund(block, player, lock.get());
        }
    }
}
