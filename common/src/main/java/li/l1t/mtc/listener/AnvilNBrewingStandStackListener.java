/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.listener;

import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class AnvilNBrewingStandStackListener implements Listener {

    @EventHandler
    public void onInvClickAnvil(InventoryClickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        HumanEntity he = e.getWhoClicked();
        if (!(he instanceof Player)) {
            return; //lol
        }
        Player plr = (Player) he;
        Inventory inv = e.getInventory();
        if (!inv.getType().equals(InventoryType.ANVIL)) {
            return;
        }
        if ((e.getCursor() != null && e.getCursor().getAmount() > 1) || (e.getCurrentItem() != null && e.getCurrentItem().getAmount() > 1)
                || e.getAction().equals(InventoryAction.HOTBAR_SWAP) || e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
            plr.sendMessage(MTC.chatPrefix + "Du darfst im Amboss keine gestackten Items verwenden. (Bugusinggefahr <3)");
            plr.closeInventory();
            e.setCancelled(true);
            //  return;
        } else {
            if ((e.getCursor() == null || e.getCursor().getType() == Material.AIR)
                    || ConfigHelper.getAnvilAllowedItems().contains(e.getCursor().getTypeId())) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR
                        && !ConfigHelper.getAnvilAllowedItems().contains(e.getCurrentItem().getTypeId())) {
                    plr.sendMessage(MTC.chatPrefix + "Du darfst dieses Item im Amboss nicht verwenden:§e " + e.getCurrentItem().getType().toString());
                    plr.closeInventory();
                    e.setCancelled(true);
                    // return;
                }
            } else {
                plr.sendMessage(MTC.chatPrefix + "Du darfst dieses Item im Amboss nicht verwenden:§a " + e.getCursor().getType().toString());
                plr.closeInventory();
                e.setCancelled(true);
                //  return;
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClickBrewingStand(InventoryClickEvent e) {
        HumanEntity he = e.getWhoClicked();
        if (!(he instanceof Player)) {
            return; //lol
        }
        Player plr = (Player) he;
        Inventory inv = e.getInventory();
        if (!inv.getType().equals(InventoryType.BREWING)) {
            return;
        }

        int maxAmount = getPermittedStackAmount(plr);

        if ((e.getCursor() != null && e.getCursor().getAmount() > maxAmount) || (e.getCurrentItem() != null && e.getCurrentItem().getAmount() > maxAmount)
                || e.getAction().equals(InventoryAction.HOTBAR_SWAP) || e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
            plr.sendMessage(MTC.chatPrefix + "Du darfst im Braustand keine gestackten Items verwenden.");
            plr.closeInventory();
            e.setCancelled(true);
        }
    }

    private int getPermittedStackAmount(Player plr) {
        return ConfigHelper.getBrewStackCheckpoints().stream()
                .filter(checkpoint -> hasPermissionForAmount(plr, checkpoint))
                .findFirst()
                .orElse(1);
    }

    private boolean hasPermissionForAmount(Player plr, int amount) {
        return plr.hasPermission("mtc.brewstack." + amount);
    }
}
