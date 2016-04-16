/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import com.google.common.base.Preconditions;
import io.github.xxyy.lib.guava17.collect.HashBasedTable;
import io.github.xxyy.lib.guava17.collect.ImmutableTable;
import io.github.xxyy.lib.guava17.collect.Table;
import io.github.xxyy.mtc.api.MTCPlugin;
import io.github.xxyy.mtc.misc.ClearCacheBehaviour;
import io.github.xxyy.mtc.module.fulltag.FullTagModule;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import io.github.xxyy.mtc.module.shop.manager.DiscountManager;
import io.github.xxyy.mtc.yaml.ManagedConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * An extension of {@link io.github.xxyy.mtc.yaml.ManagedConfiguration} which supports reading and writing of
 * {@link ShopItem}s.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
public class ShopItemConfiguration extends ManagedConfiguration implements ShopItemManager {
    private final MTCPlugin plugin;
    private final FullTagModule fullTagModule;
    private final DiscountManager discountManager;
    private Table<Material, Byte, ShopItem> shopItems = HashBasedTable.create(); //maps Material to data val, -1 = any
    private Map<String, ShopItem> itemAliases = new HashMap<>(Material.values().length); //stored in lower case

    protected ShopItemConfiguration(File file, MTCPlugin plugin, FullTagModule fullTagModule) {
        super(file);
        this.plugin = plugin;
        this.fullTagModule = fullTagModule;
        discountManager = new DiscountManager();
    }

    protected ShopItemConfiguration(File file, ShopModule module) {
        this(file, module.getPlugin(), module.getFullTagModule());
    }

    public static ShopItemConfiguration fromFile(File file, ClearCacheBehaviour behaviour, ShopModule module) {
        Validate.notNull(file, "File cannot be null");

        ManagedConfiguration.ensureReadable(file);
        ShopItemConfiguration config = new ShopItemConfiguration(file, module);
        config.setClearCacheBehaviour(behaviour);
        config.tryLoad();

        return config;
    }

    public static ShopItemConfiguration fromDataFolderPath(String filePath, ClearCacheBehaviour behaviour, ShopModule module) {
        Preconditions.checkNotNull(module.getPlugin(), "plugin");
        File file = new File(module.getPlugin().getDataFolder(), filePath);
        return fromFile(file, behaviour, module);
    }

    @Override
    public ShopItem getItem(String input) {
        input = input.toLowerCase(Locale.GERMAN); //ß -> ss
        ShopItem item = itemAliases.get(input); //Check aliases first
        if (item != null) { // Note that we do *not* support data values for aliases since those may be specific to that
            return item;    // value - Users can manually specify their aliases with colon notation though.
        }

        String[] parts = input.split(":", 2); //name:data notation, where data is a byte
        Material material = Material.matchMaterial(parts[0]); //This also checks for IDs! (although deprecated)
        byte dataValue = 0;

        if (material == null) { //Nothing matched by that name :(
            return null;
        }

        if (parts.length > 1) { //We got a data value
            String dataPart = parts[1];
            if (!dataPart.isEmpty()
                    && (StringUtils.isNumeric(dataPart)
                    || (dataPart.startsWith("-") //TODO: Does explicitly specifying a wildcard item allow for exploits?
                    && StringUtils.isNumeric(dataPart.substring(1))))) {
                dataValue = Byte.parseByte(dataPart);
            }
        }

//        if (dataValue < 0) { //invalid data value, user data value input -1 may not be correct
//            return null;
//        }

        return getItem(material, dataValue);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ShopItem getItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || isTradeProhibited(stack)) {
            return null;
        }
        return getItem(stack.getType(), stack.getData().getData());
    }

    @Override
    public ShopItem getItem(Player plr, String input) {
        Preconditions.checkNotNull(plr, "plr");
        Preconditions.checkNotNull(input, "input");
        if (input.equalsIgnoreCase("hand")) {
            return getItem(plr.getItemInHand());
        } else {
            return getItem(input);
        }
    }

    @Override
    public ShopItem getItem(Material material, byte dataValue) {
        Preconditions.checkArgument(dataValue >= 0, "dataValue must be positive: {}", dataValue);
        ShopItem foundItem = shopItems.get(material, dataValue); //check specific values before wildcard
        if (foundItem != null || dataValue == ShopItem.WILDCARD_DATA_VALUE) { //wildcard not found, ne need to check again
            return foundItem;
        }
        return getWildcardItem(material);
    }

    @Override
    public ShopItem getWildcardItem(Material material) {
        return shopItems.get(material, ShopItem.WILDCARD_DATA_VALUE);
    }

    @Override
    public boolean isTradeProhibited(ItemStack stack) {
        return (fullTagModule != null && fullTagModule.isFullItem(stack)) ||
                !stack.getEnchantments().isEmpty();
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
                .forEach(alt -> this.itemAliases.put(alt.toLowerCase(Locale.GERMAN), item));
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

    @Override
    public double getBuyCost(ShopItem shopItem) {
        return discountManager.getBuyCost(shopItem);
    }

    @Override
    public double getSellWorth(ShopItem shopItem) {
        Preconditions.checkNotNull(shopItem, "shopItem");
        return shopItem.getSellWorth(); //nothing to change here....yet
    }

    public Table<Material, Byte, ShopItem> getShopItemTable() {
        return ImmutableTable.copyOf(shopItems);
    }

    @Override
    public Map<String, ShopItem> getItemAliases() {
        return itemAliases;
    }

    @Override
    public List<ShopItem> getItems() {
        return new ArrayList<>(shopItems.values());
    }

    public MTCPlugin getPlugin() {
        return plugin;
    }

    @Override
    public DiscountManager getDiscountManager() {
        return discountManager;
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
        removeItem(item);
        storeItem(item);
        asyncSave(plugin);
    }

    private void loadItems() {
        shopItems.clear();
        itemAliases.clear();

        getKeys(false).stream()
                .map(this::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(sec -> {
                    try {
                        return ShopItem.deserialize(sec, this);
                    } catch (Exception ex) {
                        plugin.getLogger().log(Level.WARNING, "Couldn't deserialize an invalid shop item at " + sec.getName() + ", omitting: ", ex);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .forEach(this::storeItem);
    }

    private void saveItems() {
        shopItems.values().forEach(msi -> msi.serializeToSection(this)); //Sets stuff directly
    }
}
