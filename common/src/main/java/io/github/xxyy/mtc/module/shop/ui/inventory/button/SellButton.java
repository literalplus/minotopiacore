/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop.ui.inventory.button;

import com.google.common.base.Preconditions;
import io.github.xxyy.common.util.inventory.ItemStackFactory;
import io.github.xxyy.mtc.hook.VaultHook;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopMenu;
import io.github.xxyy.mtc.module.shop.ui.inventory.ShopSellMenu;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Button which sells all the items currently present in a
 * {@link io.github.xxyy.mtc.module.shop.ui.inventory.ShopSellMenu}.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-04-18
 */
public class SellButton implements MenuButton {
    private final ShopPriceCalculator priceCalculator;

    public SellButton(ShopPriceCalculator priceCalculator) {
        this.priceCalculator = priceCalculator;
    }

    @Override
    public ItemStack getItemStack(ShopMenu menu) {
        return new ItemStackFactory(Material.WOOL)
                .woolColor(DyeColor.LIME)
                .displayName("§3§lJetzt verkaufen!")
                .lore("§6Gesamtwert:")
                .lore("§e" + calculateMenuWorth(menu))
                .produce();
    }

    @Override
    public void handleMenuClick(InventoryClickEvent evt, ShopMenu menu) {
        Preconditions.checkArgument(menu instanceof ShopSellMenu,
                "menu must be ShopSellMenu, got %s", menu.getClass());

        ShopSellMenu sellMenu = (ShopSellMenu) menu;
        double totalWorth = calculateMenuWorth(menu);
        VaultHook vaultHook = menu.getModule().getPlugin().getVaultHook();
        if (vaultHook != null) {
            vaultHook.depositPlayer(menu.getPlayer(), totalWorth);
            sellMenu.clearCanvas();
            menu.getModule().getTextOutput()
                    .sendPrefixed(menu.getPlayer(), "Du hast durch deinen Verkauf §e" +
                            ShopStringAdaptor.getCurrencyString(totalWorth) +
                            "§6 eingenommen.");
        } else {
            menu.getPlayer().sendMessage("§cKonnte nicht mit dem Geldplugin reden :(");
        }
    }

    private double calculateMenuWorth(ShopMenu menu) {
        Preconditions.checkArgument(menu instanceof ShopSellMenu,
                "menu must be ShopSellMenu, got %s", menu.getClass());
        ItemStack[] contents = menu.getInventory().getContents();

        return priceCalculator.sumPrices(
                Arrays.asList(contents),
                TransactionType.SELL
        );
    }
}
