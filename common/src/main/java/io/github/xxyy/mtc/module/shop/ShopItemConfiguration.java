/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import io.github.xxyy.lib.guava17.collect.HashBasedTable;
import io.github.xxyy.lib.guava17.collect.ImmutableTable;
import io.github.xxyy.lib.guava17.collect.Table;
import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * An extension of {@link io.github.xxyy.mtc.yaml.ManagedConfiguration} which supports reading and writing of
 * {@link ShopItem}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
class ShopItemConfiguration extends ManagedConfiguration implements ShopItemManager {
    private final MTC plugin;
    private Table<Material, Byte, ShopItem> shopItems = HashBasedTable.create(); //maps Material to data val, -1 = any
    private Map<String, ShopItem> itemAliases = new HashMap<>(Material.values().length);

    protected ShopItemConfiguration(File file, MTC plugin) {
        super(file);
        this.plugin = plugin;
    }

    public static ShopItemConfiguration fromFile(File file, ClearCacheBehaviour behaviour, MTC plugin) {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IllegalStateException("Couldn't create managed config file's parent dirs for some reason: " + file.getAbsolutePath()); //Sometimes I hate Java's backwards compat
            }
            try {
                if (!file.createNewFile()) {
                    throw new IOException("Couldn't create managed config file for some reason: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new IllegalStateException("Caught IOException", e);
            }
        }

        ShopItemConfiguration config = new ShopItemConfiguration(file, plugin);
        config.setClearCacheBehaviour(behaviour);
        config.tryLoad();

        return config;
    }

    public static ShopItemConfiguration fromDataFolderPath(String filePath, ClearCacheBehaviour behaviour, MTC plugin) {
        File file = new File(plugin.getDataFolder(), filePath);
        return fromFile(file, behaviour, plugin);
    }

    @Override
    public ShopItem getItem(String input) {
        ShopItem item = itemAliases.get(input); //Check aliases first
        if (item != null) { // Note that we do *not* support data values for aliases since those may be specific to that
            return item;    // value - Users can manually specify their aliases with dot notation though.
        }

        String[] parts = input.split(":", 2); //name:data notation, where data is a byte
        Material material = Material.matchMaterial(parts[0]); //This also checks for IDs! (although deprecated)
        byte dataValue = 0;

        if (material == null) { //Nothing matched by that name :(
            return null;
        }

        if (parts.length > 1) {
            String dataPart = parts[1];
            if (!dataPart.isEmpty()
                    && (StringUtils.isNumeric(dataPart)
                    || ((dataPart.startsWith("-") || dataPart.startsWith("+"))
                        && StringUtils.isNumeric(dataPart.substring(1))))) {
                dataValue = Byte.parseByte(dataPart);
            }
        }
        if (dataValue < 0) { //invalid data value, uer data value input -1 may not be correct
            return null;
        }

        return getItem(material, dataValue);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ShopItem getItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return null;
        }
        return getItem(stack.getType(), stack.getData().getData());
    }

    @Override
    public ShopItem getItem(Material material, byte dataValue) {
        ShopItem item = shopItems.get(material, dataValue); //check specific values before wildcard
        if (item != null) {
            return item;
        }
        return shopItems.get(material, (byte) -1); //catch-all wildcard thingy
    }

    /**
     * Attempts to store an item in the configuration.
     *
     * @param item the item to store
     * @throws IllegalArgumentException if an item with that {@link ShopItem#getSerializationName()} already exists
     */
    public void storeItem(ShopItem item) {
        String path = item.getSerializationName();

        if (getItem(path) != null) {
            throw new IllegalArgumentException("An item with this name is already stored: " + path);
        }

        shopItems.put(item.getMaterial(), item.getDataValue(), item);
        item.getAliases()
                .forEach(alt -> this.itemAliases.put(alt, item));
    }

    /**
     * Attempts to remove an item from the configuration.
     *
     * @param item the item to remove
     * @return whether such item was found and removed
     * @throws NullPointerException if item is null
     */
    public boolean removeItem(ShopItem item) {
        Validate.notNull(item, "item");
        Table.Cell<Material, Byte, ShopItem> key = shopItems.cellSet().stream()
                .filter(e -> e.getValue().equals(item))
                .findAny().orElse(null);

        if (key == null) {
            return false;
        }

        shopItems.remove(key.getRowKey(), key.getColumnKey());

        itemAliases.values()
                .removeIf(Predicate.isEqual(item));

        return true;
    }

    public Table<Material, Byte, ShopItem> getShopItemTable() {
        return ImmutableTable.copyOf(shopItems);
    }

    @Override
    public Map<String, ShopItem> getItemAliases() {
        return itemAliases;
    }

    public MTC getPlugin() {
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

    private void loadItems() {
        shopItems.clear();
        itemAliases.clear();

        getKeys(false).stream()
                .map(this::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(sec -> {
                    try {
                        return ShopItem.deserialize(sec);
                    } catch (Exception ex) {
                        plugin.getLogger().log(Level.WARNING, "Couldn't deserialize an invalid shop item at " + sec.getName() + ", omitting: ", ex);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .forEach(item -> {
                    storeItem(item);
                    shopItems.put(item.getMaterial(), item.getDataValue(), item);
                    item.getAliases().stream()
                            .forEach(alt -> itemAliases.put(alt, item));
                });
    }

    private void saveItems() {
        shopItems.values().forEach(msi -> msi.serializeToSection(this)); //Sets stuff directly
    }
}
