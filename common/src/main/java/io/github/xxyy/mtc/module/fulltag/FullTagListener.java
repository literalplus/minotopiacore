/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.fulltag;

import io.github.xxyy.common.util.LocationHelper;
import io.github.xxyy.mtc.logging.LogManager;
import io.github.xxyy.mtc.module.fulltag.model.FullInfo;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Listens for some inventory events and tracks Full items. Also removes unknown or deleted ones on encounter.
 *
 * @author <a href="https://xxyy.github.io/>xxyy</a>
 */
public final class FullTagListener implements Listener {
    private static final Logger LOGGER = LogManager.getLogger(FullTagListener.class);
    private static final Random RANDOM = new Random();
    private final FullTagModule module;

    public FullTagListener(FullTagModule module) {
        this.module = module;
    }

    private void checkHeldFullsForDuplicates(@Nonnull Player plr) {
        Set<Integer> foundIds = new HashSet<>();
        for (ItemStack stack : plr.getInventory().getArmorContents()) {
            int fullId = module.getFullId(stack);
            if (fullId < 0) {
                continue;
            }
            foundIds.add(fullId); //Can't already exist
        }
        for (int i = 0; i < plr.getInventory().getContents().length; i++) {
            ItemStack stack = plr.getInventory().getItem(i);
            int fullId = module.getFullId(stack);
            if (fullId < 0) {
                continue;
            }
            if (!foundIds.add(fullId)) {
                message(plr, "§cEine Full ist in deinem Inventar zweimal vorhanden gewesen! Für " +
                        "etwaige Beschwerden notiere dir bitte unbedingt die aktuelle Uhrzeit und die Zahl %d!", fullId);
                LOGGER.info("[Dupe Full] Removed #{} @ {} / {} / {}",
                        fullId, plr.getUniqueId(), plr.getName());
                plr.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            if(stack.getAmount() > 1) {
                message(plr, "§cDiese Full war auf %d gestackt und musste daher auf 1 reduziert werden. Für etwaige " +
                                "Beschwerden notiere dir bitte unbedingt die aktuelle Uhrzeit und die Zahl %d!",
                        stack.getAmount(), fullId);
                LOGGER.info("[Stacked Full] Unstacked #{} / {} / {} | stack size={} (check)",
                        fullId, plr.getUniqueId(), plr.getName(), stack.getAmount());
                stack.setAmount(1);
                plr.getInventory().setItem(i, stack);
            }
        }
    }

    /**
     * Checks whether the given item is a malicious Full item and should be removed. Also logs a nice message to Log4j2
     * and messages the player if something is wrong. Also notifies the {@link FullInfo}.
     *
     * @param stack     the stack to check
     * @param plr       the player involved in this action, for yelling at them
     * @param location  the location at which this action primarily occurred
     * @param locCode   an arbitrary string describing the action
     * @param inventory the inventory involved in this action, if any
     * @param callback  the callback to be called when the computation has finished
     */
    private void checkFull(@Nonnull ItemStack stack, @Nullable Player plr, Location location, String locCode,
                           Inventory inventory, @Nonnull BiConsumer<CheckResult, FullInfo> callback) {
        int fullId = module.getFullId(stack);
        if (fullId < 0) {
            callback.accept(CheckResult.NOT_A_FULL, null);
            return;
        }
        String plrName = plr == null ? "null" : plr.getName();
        String plrUid = plr == null ? "null" : plr.getUniqueId().toString();
        FullInfo info = module.getRegistry().getById(fullId); //TODO: database call in main thread
        if (info == null) {
            message(plr, "§cDiese Full ist nicht in der Datenbank vorhanden und musste daher entfernt werden. Für " +
                    "etwaige Beschwerden notiere dir bitte unbedingt die aktuelle Uhrzeit und die Zahl %d!", fullId);
            LOGGER.info("[Invalid Full] Removed #{} @ {} / {} / {}",
                    fullId, LocationHelper.prettyPrint(location), plrUid, plrName);
            callback.accept(CheckResult.INVALID, null);
            return;
        }

        info.notifyEncounter(location, locCode, plr == null ? null : plr.getUniqueId(), inventory);

        if (stack.getAmount() > 1) {
            message(plr, "§cDiese Full war auf %d gestackt und musste daher auf 1 reduziert werden. Für etwaige " +
                            "Beschwerden notiere dir bitte unbedingt die aktuelle Uhrzeit und die Zahl %d!",
                    stack.getAmount(), fullId);
            LOGGER.info("[Stacked Full] Unstacked #{} @ {} / {} / {} | stack size={}",
                    fullId, LocationHelper.prettyPrint(location), plrUid, plrName, stack.getAmount());
            stack.setAmount(1);
            callback.accept(CheckResult.STACKED, info);
        } else {
            callback.accept(CheckResult.VALID, info);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDespawn(@Nonnull ItemDespawnEvent evt) {
        checkFull(evt.getEntity().getItemStack(), null, evt.getEntity().getLocation(), "despawn", null,
                (res, info) -> {
                    if (info != null) {
                        LOGGER.info("[Despawned Full] Invalidated #{} @ {}",
                                info.getId(), LocationHelper.prettyPrint(evt.getEntity().getLocation()));
                        info.setValid(false);
                        module.getRegistry().save(info);
                    }
                });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(@Nonnull EntityDamageEvent evt) {
        if (evt.getEntityType() != EntityType.DROPPED_ITEM) {
            return;
        }
        LOGGER.debug("dmg - {} ({})", evt.getEntityType(), evt.getEntity().getUniqueId());
        checkFull(((Item) evt.getEntity()).getItemStack(), null, evt.getEntity().getLocation(), "death", null,
                (res, info) -> {
                    if (info != null) {
                        LOGGER.info("[Died Full] Invalidated #{} @ {}",
                                info.getId(), LocationHelper.prettyPrint(evt.getEntity().getLocation()));
                        info.setValid(false);
                        module.getRegistry().save(info);
                        evt.getEntity().remove();
                    }
                });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(@Nonnull PlayerDropItemEvent evt) {
        checkFull(evt.getItemDrop().getItemStack(), evt.getPlayer(), evt.getItemDrop().getLocation(), "drop", null,
                (res, info) -> {
                    if (res.shouldRemove()) {
                        evt.getItemDrop().remove();
                    }
                });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInvClick(@Nonnull InventoryClickEvent evt) {
        HumanEntity who = evt.getWhoClicked();
        Inventory inv = evt.getInventory();
        switch (evt.getAction()) {
            case NOTHING:
            case UNKNOWN:
            case CLONE_STACK: //handled client side :(
                return;
            default:
        }

        switch (evt.getSlotType()) {
            case CRAFTING:
            case FUEL:
            case RESULT:
            case OUTSIDE: //handled by drop listener
                return;
            default:
                if (!(who instanceof Player)) {
                    return;
                }
                Location loc = inv.getHolder() instanceof Block ? ((Block) inv.getHolder()).getLocation() : who.getLocation();
                checkFull(evt.getCurrentItem(), (Player) who, loc,
                        "invclick_" + evt.getAction() + "_in_" + evt.getClickedInventory().getType(), //apparently no way to find where it's placed
                        inv, (res, info) -> {
                            if (res.shouldRemove()) {
                                if (evt.getCurrentItem() != null) {
                                    evt.setCurrentItem(new ItemStack(Material.AIR));
                                    evt.getCurrentItem().setAmount(0);
                                }
                            } else if(res == CheckResult.STACKED) {
                                evt.getCurrentItem().setAmount(1);
                                evt.setCancelled(true);
                            }
                        });
                if (RANDOM.nextInt(32) == 12) {
                    checkHeldFullsForDuplicates((Player) who);
                }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(@Nonnull PlayerPickupItemEvent evt) {
        checkFull(evt.getItem().getItemStack(), evt.getPlayer(), evt.getItem().getLocation(), "pickup", null,
                (res, info) -> {
                    if (res.shouldRemove()) {
                        evt.getItem().setItemStack(new ItemStack(Material.AIR));
                        evt.getItem().remove();
                        evt.setCancelled(true);
                    }
                });
    }

    private void message(@Nullable Player player, @Nonnull String message, Object... args) {
        if (player != null) {
            player.sendMessage(module.getPlugin().getChatPrefix() + String.format(message, args));
        }
    }

    private enum CheckResult {
        NOT_A_FULL(false),
        STACKED(false),
        INVALID(true),
        VALID(false);

        private final boolean remove;

        CheckResult(boolean remove) {
            this.remove = remove;
        }

        public boolean shouldRemove() {
            return remove;
        }
    }
}
