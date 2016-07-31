/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.button;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopListMenu;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopMenu;

/**
 * Represents pagination buttons visualised through player heads to be used with {@link ShopListMenu}.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-17
 */
public class PaginationButton implements MenuButton<ShopListMenu> {
    public static final PaginationButton FIRST_PAGE = new PaginationButton(PaginationAction.FIRST);
    public static final PaginationButton PREVIOUS_PAGE = new PaginationButton(PaginationAction.PREVIOUS);
    public static final PaginationButton NEXT_PAGE = new PaginationButton(PaginationAction.NEXT);
    public static final PaginationButton LAST_PAGE = new PaginationButton(PaginationAction.LAST);

    private final PaginationAction action;
    private final ItemStack itemStack;

    PaginationButton(PaginationAction action) {
        Preconditions.checkNotNull(action, "action");
        this.action = action;
        this.itemStack = new ItemStackFactory(action.getDisplayMaterial())
                .displayName("§b" + action.getDisplayName())
                .produce();
    }

    @Override
    public ItemStack getItemStack(ShopListMenu menu) {
        return itemStack;
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopListMenu menu) {
        menu.setItemStart(action.getTargetItemStart(menu));
        menu.renderCurrentPage();
    }

    private enum PaginationAction {
        FIRST(Material.POWERED_MINECART, "<< Zur ersten Seite") {
            @Override
            int getTargetItemStart(ShopListMenu menu) {
                return firstPage();
            }
        },
        PREVIOUS(Material.MINECART, "< Zur vorherigen Seite") {
            @Override
            int getTargetItemStart(ShopListMenu menu) {
                if (menu.getCurrentItemStart() < ShopMenu.CANVAS_SIZE) {
                    return lastPage(menu);
                } else {
                    return menu.getCurrentItemStart() - ShopMenu.CANVAS_SIZE;
                }
            }
        },
        NEXT(Material.MINECART, "Zur nächsten Seite >") {
            @Override
            int getTargetItemStart(ShopListMenu menu) {
                if (menu.getCurrentItemStart() >= lastPage(menu)) {
                    return firstPage();
                } else {
                    return menu.getCurrentItemStart() + ShopMenu.CANVAS_SIZE;
                }
            }
        },
        LAST(Material.POWERED_MINECART, "Zur letzten Seite >>") {
            @Override
            int getTargetItemStart(ShopListMenu menu) {
                return lastPage(menu);
            }
        };

        private final Material displayMaterial;
        private final String displayName;

        PaginationAction(Material displayMaterial, String displayName) {
            this.displayMaterial = displayMaterial;
            this.displayName = displayName;
        }

        /**
         * Gets the index of the first item to be displayed after this button is clicked.
         *
         * @param menu the menu to get the index for
         * @return the index
         */
        abstract int getTargetItemStart(ShopListMenu menu);

        int lastPage(ShopListMenu menu) {
            if (menu.getItems().size() <= ShopMenu.CANVAS_SIZE) {
                return firstPage();
            }
            int itemsOnLastPage = menu.getItems().size() % ShopMenu.CANVAS_SIZE;
            int freeSlotsOnLastPage = ShopMenu.CANVAS_SIZE - itemsOnLastPage;
            return menu.getItems().size() - ShopMenu.CANVAS_SIZE + freeSlotsOnLastPage;
        }

        private static int firstPage() {
            return 0;
        }

        public Material getDisplayMaterial() {
            return displayMaterial;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
