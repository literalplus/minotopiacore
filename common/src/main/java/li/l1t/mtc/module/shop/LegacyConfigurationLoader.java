/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
