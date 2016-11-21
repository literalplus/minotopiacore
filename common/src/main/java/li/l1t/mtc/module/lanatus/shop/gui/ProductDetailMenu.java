/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.gui;

import li.l1t.common.inventory.SlotPosition;
import li.l1t.common.inventory.gui.ChildMenu;
import li.l1t.common.inventory.gui.TopRowMenu;
import li.l1t.common.inventory.gui.element.LambdaMenuElement;
import li.l1t.common.inventory.gui.element.Placeholder;
import li.l1t.common.inventory.gui.element.button.BackToParentButton;
import li.l1t.common.util.inventory.ItemStackFactory;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.ItemIconService;
import li.l1t.lanatus.shop.api.ProductBuyService;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


/**
 * Displays a menu for displaying a product and offers an option to purchase it.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-18-11
 */
public class ProductDetailMenu extends TopRowMenu implements ChildMenu {
    private final Product product;
    private final ProductSelectionMenu parent;
    private final ItemIconService iconService;
    private final ProductBuyService buyService;

    public ProductDetailMenu(Product product, ProductSelectionMenu parent, ItemIconService iconService, ProductBuyService buyService) {
        super(parent.getPlugin(), "§e§l" + product.getDisplayName(), parent.getPlayer());
        this.product = product;
        this.parent = parent;
        this.iconService = iconService;
        this.buyService = buyService;
        initTopRow();
    }

    @Override
    protected void initTopRow() {
        addToTopRow(0, BackToParentButton.INSTANCE);
        addToTopRow(1, new Placeholder(iconService.createIconStack(parent.getCategory())));
        addToTopRow(2, new Placeholder(iconService.createIconStack(product, false)));
        addToTopRow(3, new Placeholder(iconService.createPurchaseHelpStack()));
        addToTopRow(8, BackToParentButton.INSTANCE);
        addElement(SlotPosition.ofXY(1, 3), confirmButton());
        addElement(SlotPosition.ofXY(7, 3), abortButton());
    }

    @SuppressWarnings("deprecation")
    private LambdaMenuElement<ProductDetailMenu> confirmButton() {
        return new LambdaMenuElement<>(ProductDetailMenu.class, this::handleConfirm, confirmStack());
    }

    @NotNull
    private ItemStack confirmStack() {
        return new ItemStackFactory(new ItemStack(Material.STAINED_CLAY, 1, DyeColor.GREEN.getWoolData()))
                .displayName("§a§lBestätigen")
                .lore("§7Hier klicken, um dieses").lore("§7Produkt zu kaufen")
                .lore(" ").lore("§e" + product.getMelonsCost() + " " + melonPlural(product.getMelonsCost()))
                .produce();
    }

    private String melonPlural(int melonsCount) {
        return "Melone" + (melonsCount == 1 ? "" : "n");
    }

    @SuppressWarnings("deprecation")
    private LambdaMenuElement<ProductDetailMenu> abortButton() {
        return new LambdaMenuElement<>(ProductDetailMenu.class, this::handleAbort, abortStack());
    }

    @NotNull
    private ItemStack abortStack() {
        return new ItemStackFactory(new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getWoolData()))
                .displayName("§c§lAbbrechen")
                .lore("§7Hier klicken, um").lore("§7abzubrechen")
                .produce();
    }

    private void handleAbort(InventoryClickEvent evt, ProductDetailMenu menu) {
        getParent().open();
    }

    private void handleConfirm(InventoryClickEvent evt, ProductDetailMenu menu) {
        buyService.attemptPurchase(getPlayer(), product);
    }

    @Override
    public ProductSelectionMenu getParent() {
        return parent;
    }
}
