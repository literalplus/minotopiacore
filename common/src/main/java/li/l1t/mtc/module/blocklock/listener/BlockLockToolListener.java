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
