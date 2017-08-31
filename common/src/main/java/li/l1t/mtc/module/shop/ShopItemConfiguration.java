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

import com.google.common.base.Preconditions;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * An extension of {@link ManagedConfiguration} which supports reading and writing of {@link
 * ShopItem}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
public class ShopItemConfiguration extends ManagedConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(ShopItemConfiguration.class);
    private static final String ITEMS_PATH = "items-version-2";
    private final MTCPlugin plugin;
    private final ShopItemManager itemManager;

    protected ShopItemConfiguration(File file, MTCPlugin plugin, ShopItemManager itemManager) {
        super(file);
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    public static ShopItemConfiguration fromDataFolderPath(String filePath, ClearCacheBehaviour behaviour, ShopModule module) {
        Preconditions.checkNotNull(module.getPlugin(), "plugin");
        File file = new File(module.getPlugin().getDataFolder(), filePath);
        return fromFile(file, behaviour, module);
    }

    public static ShopItemConfiguration fromFile(File file, ClearCacheBehaviour behaviour, ShopModule module) {
        Validate.notNull(file, "File cannot be null");
        ManagedConfiguration.ensureReadable(file);
        ShopItemConfiguration config = new ShopItemConfiguration(file, module.getPlugin(), module.getItemManager());
        config.setClearCacheBehaviour(behaviour);
        config.tryLoad();
        return config;
    }

    public boolean seemsToBeInLegacyFormat() {
        return !contains(ITEMS_PATH) | getKeys(false).size() > 1;
    }

    public MTCPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        super.loadFromString(contents);
        loadItems();
    }

    @Override
    public String saveToString() {
        saveItems();
        return super.saveToString();
    }

    /**
     * Updates this configuration's caches to match the updated state of given item.
     *
     * @param item the items whose state may have been changed
     */
    public void updateItem(ShopItem item) {
        itemManager.unregisterItem(item);
        itemManager.registerItem(item);
        asyncSave(plugin);
    }

    private void loadItems() {
        itemManager.getItems().forEach(itemManager::unregisterItem);
        getList(ITEMS_PATH, new ArrayList<>()).stream()
                .filter(Objects::nonNull)
                .filter(obj -> obj instanceof ShopItem)
                .map(obj -> (ShopItem) obj)
                .forEach(itemManager::registerItem);
    }

    private void saveItems() {
        set(ITEMS_PATH, new ArrayList<>(itemManager.getItems()));
    }
}
