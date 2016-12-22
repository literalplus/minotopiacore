/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.shop;

import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.item.DataValueShopItem;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loads legacy shop items from the configuration, so that they may be converted to a more flexible format when saved
 * the next time.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-22
 */
public class LegacyConfigurationLoader {
    private static final Logger LOGGER = LogManager.getLogger(LegacyConfigurationLoader.class);
    private final ShopItemConfiguration config;
    private final ShopItemManager manager;

    public LegacyConfigurationLoader(ShopItemConfiguration config, ShopItemManager manager) {
        this.config = config;
        this.manager = manager;
    }

    public List<ShopItem> readAndDeleteLegacyItems() {
        return config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(sec -> {
                    try {
                        @SuppressWarnings("deprecation")
                        DataValueShopItem item = DataValueShopItem.deserialize(sec, manager);
                        sec.getParent().set(sec.getName(), null);
                        return item;
                    } catch (Exception e) {
                        LOGGER.warn("Error reading legacy config item {} - not deleting yet.", sec.getName(), e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
