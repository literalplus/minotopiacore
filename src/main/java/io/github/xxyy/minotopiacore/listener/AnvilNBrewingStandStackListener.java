package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
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
            plr.sendMessage("§c[MTS]§6 Du darfst im Amboss keine gestackten Items verwenden. (Bugusinggefahr <3)");
            plr.closeInventory();
            e.setCancelled(true);
            //  return;
        } else {
            if ((e.getCursor() == null || e.getCursor().getType() == Material.AIR)
                    || ConfigHelper.getAnvilAllowedItems().contains(e.getCursor().getTypeId())) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR
                        && !ConfigHelper.getAnvilAllowedItems().contains(e.getCurrentItem().getTypeId())) {
                    plr.sendMessage("§c[MTS]§6 Du darfst dieses Item im Amboss nicht verwenden:§e " + e.getCurrentItem().getType().toString());
                    plr.closeInventory();
                    e.setCancelled(true);
                    // return;
                }
            } else {
                plr.sendMessage("§c[MTS]§6 Du darfst dieses Item im Amboss nicht verwenden:§b " + e.getCursor().getType().toString());
                plr.closeInventory();
                e.setCancelled(true);
                //  return;
            }
        }

    }

    @EventHandler
    public void onInvClickBrewingStand(InventoryClickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        HumanEntity he = e.getWhoClicked();
        if (!(he instanceof Player)) {
            return; //lol
        }
        Player plr = (Player) he;
        Inventory inv = e.getInventory();
        if (!inv.getType().equals(InventoryType.BREWING)) {
            return;
        }
        if ((e.getCursor() != null && e.getCursor().getAmount() > 1) || (e.getCurrentItem() != null && e.getCurrentItem().getAmount() > 1)
                || e.getAction().equals(InventoryAction.HOTBAR_SWAP) || e.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
            plr.sendMessage("§c[MTS]§6 Du darfst im Braustand keine gestackten Items verwenden. (Bugusinggefahr <3)");
            plr.closeInventory();
            e.setCancelled(true);
        }
    }
}
