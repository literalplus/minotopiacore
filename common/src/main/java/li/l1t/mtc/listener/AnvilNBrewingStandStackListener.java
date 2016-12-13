/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.listener;

import com.google.common.base.Preconditions;
import li.l1t.mtc.ConfigHelper;
import li.l1t.mtc.MTC;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class AnvilNBrewingStandStackListener implements Listener {

    @EventHandler
    public void onInvClickAnvil(InventoryClickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        HumanEntity he = e.getWhoClicked();
        if (!(he instanceof Player)) {
            return; //casually implying this will ever happen
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

    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        int[] slotAmounts = new int[3];
        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                slotAmounts[i] = item.getAmount();
            }
        }
        MTC plugin = MTC.instance();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < 3; i++) {
                if (slotAmounts[i] > 1) {
                    ItemStack result = inventory.getItem(i);
                    result.setAmount(slotAmounts[i]);
                    inventory.setItem(i, result);
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClickBrewingStand(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return; //lol
        }
        Player player = (Player) humanEntity;
        if (isClickInPotionSlotOfBrewingStandWithItemOnCursor(event)) {
            handlePotionSlotClick(event, player);
        }
    }

    private boolean isClickInPotionSlotOfBrewingStandWithItemOnCursor(InventoryClickEvent e) {
        return e.getClickedInventory() != null && e.getClickedInventory().getType().equals(InventoryType.BREWING) &&
                e.getCursor() != null && e.getRawSlot() <= 3;
    }

    private void handlePotionSlotClick(InventoryClickEvent event, Player player) {
        if (event.getAction() == InventoryAction.PLACE_ALL && areCursorAndSlotItemMergeable(event)) {
            mergeCursorWithSlotItem(event, getPermittedStackAmount(player));
        }
    }

    private boolean areCursorAndSlotItemMergeable(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        Preconditions.checkNotNull(cursor, "cursor");
        if (currentItem == null || currentItem.getType().equals(Material.AIR)) {
            return true;
        } else if (currentItem.getType().equals(cursor.getType())) {
            if (currentItem.getItemMeta() instanceof PotionMeta && cursor.getItemMeta() instanceof PotionMeta) {
                return currentItem.getItemMeta().equals(cursor.getItemMeta());
            }
        }
        return false;
    }

    private void mergeCursorWithSlotItem(InventoryClickEvent event, int maxAllowedStackSize) {
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        int totalAmountOfItems = cursor.getAmount() + currentItem.getAmount();
        int effectiveStackSize = Math.min(totalAmountOfItems, maxAllowedStackSize);
        event.setCurrentItem(setStackAmount(cursor, effectiveStackSize));
        event.setCursor(setStackAmount(cursor, totalAmountOfItems - effectiveStackSize));
        event.setCancelled(true);
    }

    private ItemStack setStackAmount(ItemStack baseStack, int amount) {
        if(amount <= 0) {
            return new ItemStack(Material.AIR, 0);
        }
        ItemStack cloned = baseStack.clone();
        cloned.setAmount(amount);
        return cloned;
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
