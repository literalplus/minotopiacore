/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.truefalse;

import li.l1t.common.misc.XyLocation;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listens for events related to the TrueFalse game.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-07
 */
class TrueFalseWandListener implements Listener {
    private TrueFalseModule module;

    public TrueFalseWandListener(TrueFalseModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent evt) {
        ItemStack item = evt.getPlayer().getItemInHand();
        if (!module.hasBoundarySession(evt.getPlayer().getUniqueId()) ||
                (evt.getAction() != Action.RIGHT_CLICK_BLOCK && evt.getAction() != Action.LEFT_CLICK_BLOCK) ||
                item == null || item.getType() != TrueFalseModule.MAGIC_WAND_MATERIAL) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (item.hasItemMeta() && meta.hasDisplayName() &&
                meta.getDisplayName().equals(TrueFalseModule.MAGIC_WAND_NAME)) {
            if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                module.setSecondBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                evt.getPlayer().sendMessage("§aZweiter Eckpunkt gesetzt!");
                evt.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                module.endBoundarySession(evt.getPlayer().getUniqueId());
            } else {
                module.setFirstBoundary(new XyLocation(evt.getClickedBlock().getLocation()));
                evt.getPlayer().sendMessage("§aErster Eckpunkt gesetzt!");
            }

            evt.setCancelled(true);
        }
    }
}
