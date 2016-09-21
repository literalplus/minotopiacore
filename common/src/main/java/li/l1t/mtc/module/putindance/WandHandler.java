/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
    private final Set<UUID> boundarySessions = new HashSet<>();
    private final PutinDanceConfig config;

    WandHandler(Plugin plugin, PutinDanceConfig config) {
        this.config = config;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void startBoundarySession(Player player) {
        boundarySessions.add(player.getUniqueId());
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
        config.setSecondBoardBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Zweiter Eckpunkt gesetzt!");
        endBoundarySession(player);
    }

    public void endBoundarySession(Player player) {
        if (isWandItem(player.getItemInHand())) {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
        boundarySessions.remove(player.getUniqueId());
    }

    private void handleWandRightClick(PlayerInteractEvent evt, Player player) {
        config.setFirstBoardBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Erster Eckpunkt gesetzt!");
    }

    private boolean isWandInteraction(PlayerInteractEvent evt, ItemStack item) {
        return hasBoundarySession(evt.getPlayer().getUniqueId()) &&
                isClickAction(evt.getAction()) && isWandItem(item);
    }

    public boolean hasBoundarySession(UUID playerId) {
        return boundarySessions.contains(playerId);
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
