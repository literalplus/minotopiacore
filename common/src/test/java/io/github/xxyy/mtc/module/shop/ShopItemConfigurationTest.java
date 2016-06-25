/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import io.github.xxyy.lib.guava17.collect.Table;
import io.github.xxyy.mtc.MTC;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the configuration file logic for the Shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 25/05/15
 */
public class ShopItemConfigurationTest {
    private static ShopItem POTATO = new ShopItem(null, 3, 1, Material.POTATO, ShopItem.WILDCARD_DATA_VALUE,
            Lists.newArrayList("yolo", "potatos", "Kartoffel"), ShopItem.NOT_DISCOUNTABLE);
    private static ShopItem DIAMOND = new ShopItem(null, 7, 5, Material.DIAMOND, (short) 42,
            Lists.newArrayList("[dai-mond]", "Diamant", "dia:shiny"), 6);
    private ShopItemConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = new ShopItemConfiguration(mock(File.class), mock(MTC.class), null);
        populateWithExamples(config);
    }

    @Test
    public void testLoadFromString() throws Exception {
        String serialized = config.saveToString();
        Table<Material, Short, ShopItem> items = config.getShopItemTable(); //FIXME: exposing implementation details
        Map<String, ShopItem> aliases = config.getItemAliases();

        config.loadFromString(serialized);
        assertThat("Items not loaded correctly/equals not functioning", config.getShopItemTable(), is(items));
        assertThat("Aliases not loaded correctly/equals not functioning", config.getItemAliases(), is(aliases));
    }

    @Test
    public void testGetItem() {
        subTestGetBySerializationName();
        subTestGetByAliases();
        subTestGetByDataValues();
        subTestGetByMaterial();
        subTestGetByStack();
    }

    private void subTestGetBySerializationName() {
        assertThat("Potato not mapped to serialization name", config.getItem(POTATO.getSerializationName()), is(POTATO));
        assertThat("Diamond not mapped to serialization name", config.getItem(DIAMOND.getSerializationName()), is(DIAMOND));
    }

    private void subTestGetByMaterial() {
        assertThat("Potato not mapped to material", config.getItem(Material.POTATO.name()), is(POTATO));
        assertThat("Diamond mapped to plain material", config.getItem(Material.DIAMOND.name()), is(nullValue()));
    }

    @SuppressWarnings("deprecation")
    private void subTestGetByStack() {
        assertThat("Potato not mapped to stack",
                config.getItem(new ItemStack(Material.POTATO)), is(POTATO));
        assertThat("Diamond wrongly mapped to arbitrary stack",
                config.getItem(new ItemStack(Material.DIAMOND)), is(nullValue()));
        assertThat("Diamond not mapped to stack with correct data value",
                config.getItem(new ItemStack(Material.DIAMOND, 2, (short) 42)), is(DIAMOND));
    }

    private void subTestGetByAliases() {
        POTATO.getAliases().forEach(al ->
                assertThat("Potato not mapped to alias: " + al, config.getItem(al), is(POTATO)));
        DIAMOND.getAliases().forEach(al ->
                assertThat("Diamond not mapped to alias: " + al, config.getItem(al), is(DIAMOND)));
    }

    private void subTestGetByDataValues() {
        //test that items including all data values (-1) are mapped to all data values
        assertThat("Potato not mapped to data value 0", config.getItem(Material.POTATO.name() + ":0"), is(POTATO));
        assertThat("Potato not mapped to data value 1", config.getItem(Material.POTATO.name() + ":1"), is(POTATO));
        assertThat("Potato not mapped to data value 127", config.getItem(Material.POTATO.name() + ":127"), is(POTATO));
        assertThat("Potato not mapped to data value 127 (native)", config.getItem(Material.POTATO, (short) 127), is(POTATO));

        //reverse case - items with specific data value should only be mapped to that value
        assertThat("Diamond mapped to data value 0", config.getItem(Material.DIAMOND.name() + ":0"), is(nullValue()));
        assertThat("Diamond mapped to data value 1", config.getItem(Material.DIAMOND.name() + ":1"), is(nullValue()));
        assertThat("Diamond mapped to data value 127", config.getItem(Material.DIAMOND.name() + ":127"), is(nullValue()));
        assertThat("Diamond mapped to data value 127 (native)", config.getItem(Material.DIAMOND, (short) 127), is(nullValue()));
        assertThat("Diamond not mapped to own data value",
                config.getItem(Material.DIAMOND.name() + ":" + DIAMOND.getDataValue()), is(DIAMOND));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWildcardDataValueException() {
        //test that the item is not wrongly mapped to -1 (special value)
        System.out.println(config.getItem(Material.POTATO.name() + ":-1"));
    }

    //store

    //remove

    private void populateWithExamples(ShopItemConfiguration someConfig) {
        someConfig.storeItem(DIAMOND);
        someConfig.storeItem(POTATO);
    }
}
