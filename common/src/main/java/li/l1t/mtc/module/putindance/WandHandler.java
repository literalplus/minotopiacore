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

package li.l1t.mtc.module.putindance;

import li.l1t.common.collections.Couple;
import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles definition of board boundaries using a wand item.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
class WandHandler implements Listener {
    private static final Material WAND_MATERIAL = Material.BLAZE_ROD;
    private static final String WAND_NAME = "§3Putins Zauberstab";
    private final Map<UUID, Couple<XyLocation>> boundarySessions = new HashMap<>();
    private final PutinDanceModule module;

    WandHandler(PutinDanceModule module) {
        this.module = module;
        module.getPlugin().getServer().getPluginManager().registerEvents(this, module.getPlugin());
    }

    public void startBoundarySession(Player player) {
        boundarySessions.put(player.getUniqueId(), Couple.of(null, null));
        player.getInventory().addItem(createWandItem());
    }

    private ItemStack createWandItem() {
        return new ItemStackFactory(WAND_MATERIAL)
                .displayName(WAND_NAME)
                .lore(Arrays.asList(
                        "§7Right click a block to set first boundary",
                        "§7Left click a block to set second boundary"
                ))
                .produce();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent evt) {
        if (!isWandInteraction(evt, evt.getPlayer().getItemInHand())) {
            return;
        }
        evt.setCancelled(true);
        handleWandInteraction(evt, evt.getPlayer());
    }

    private void handleWandInteraction(PlayerInteractEvent evt, Player player) {
        if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            handleWandLeftClick(evt, player);
        } else {
            handleWandRightClick(evt, player);
        }
    }

    private void handleWandLeftClick(PlayerInteractEvent evt, Player player) {
        cacheSecondBoundary(player, evt.getClickedBlock().getLocation());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Zweiter Eckpunkt gesetzt!");
    }

    private void handleWandRightClick(PlayerInteractEvent evt, Player player) {
        cacheFirstBoundary(player, evt.getClickedBlock().getLocation());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Erster Eckpunkt gesetzt!");
    }

    private void cacheFirstBoundary(Player player, Location location) {
        boundarySessions.computeIfPresent(
                player.getUniqueId(),
                (key, oldValue) -> oldValue.withLeft(XyLocation.of(location))
        );
        tryFinishBoundarySession(player);
    }

    private void cacheSecondBoundary(Player player, Location location) {
        boundarySessions.computeIfPresent(
                player.getUniqueId(),
                (key, oldValue) -> oldValue.withRight(XyLocation.of(location))
        );
        tryFinishBoundarySession(player);
    }

    private void tryFinishBoundarySession(Player player) {
        if (boundarySessions.containsKey(player.getUniqueId())) {
            Couple<XyLocation> locations = boundarySessions.get(player.getUniqueId());
            if (locations.getLeft() != null && locations.getRight() != null) {
                finishBoundarySession(player);
            }
        }
    }

    private void finishBoundarySession(Player player) {
        Couple<XyLocation> locations = boundarySessions.remove(player.getUniqueId());
        module.setBoardBoundaries(locations.getLeft(), locations.getRight());
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Spielfeldränder gesetzt!");
        takeWandItemFrom(player);
    }

    private void takeWandItemFrom(Player player) {
        if (isWandItem(player.getItemInHand())) {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
    }

    private boolean isWandInteraction(PlayerInteractEvent evt, ItemStack item) {
        return hasBoundarySession(evt.getPlayer().getUniqueId()) &&
                isClickAction(evt.getAction()) && isWandItem(item);
    }

    public boolean hasBoundarySession(UUID playerId) {
        return boundarySessions.containsKey(playerId);
    }

    private boolean isWandItem(ItemStack item) {
        if (item == null || item.getType() != WAND_MATERIAL || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(WAND_NAME);
    }

    private boolean isClickAction(Action action) {
        return action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK;
    }
}
