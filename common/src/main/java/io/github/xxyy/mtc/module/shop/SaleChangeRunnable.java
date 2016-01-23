/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.common.util.task.NonAsyncBukkitRunnable;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SaleChangeRunnable extends NonAsyncBukkitRunnable {
    @NotNull
    private final ShopModule module;
    private final Random random = new SecureRandom();

    public SaleChangeRunnable(@NotNull ShopModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        List<ShopItem> items = module.getItemManager().getItems().stream()
            .filter(ShopItem::canBeBought)
            .filter(ShopItem::canBeOnSale)
            .collect(Collectors.toList());
        if (items.isEmpty()) {
            module.getPlugin().getLogger().warning("[" + module.getName() + "] Could not set an item on sale, as there is no item available to be put on sale.");
        } else {
            ShopItem item = items.get(random.nextInt(items.size()));
            module.getItemManager().setItemOnSale(item);
            module.getTextOutput().broadcastItemSale(item);
            module.getTextOutput().sendItemSale(item, Bukkit.getConsoleSender());
        }
    }
}
