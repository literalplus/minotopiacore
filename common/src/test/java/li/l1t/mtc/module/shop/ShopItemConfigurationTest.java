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

import com.google.common.collect.Lists;
import li.l1t.mtc.MTC;
import li.l1t.mtc.module.shop.api.ShopItem;
import li.l1t.mtc.module.shop.api.ShopItemManager;
import li.l1t.mtc.module.shop.item.DataValueShopItem;
import li.l1t.mtc.module.shop.manager.CachingShopItemManager;
import li.l1t.mtc.module.shop.manager.DiscountManager;
import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the configuration file logic for the Shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 25/05/15
 */
public class ShopItemConfigurationTest {
    private static ShopItem POTATO = new DataValueShopItem(3, 1, Material.POTATO, DataValueShopItem.WILDCARD_DATA_VALUE,
            Lists.newArrayList("yolo", "potatos", "Kartoffel"), ShopItem.NOT_DISCOUNTABLE);
    public static final short DIAMOND_DATA_VALUE = (short) 42;
    private static ShopItem DIAMOND = new DataValueShopItem(7, 5, Material.DIAMOND, DIAMOND_DATA_VALUE,
            Lists.newArrayList("[dai-mond]", "Diamant", "dia:shiny"), 6);
    private ShopItemManager manager;
    private ShopItemConfiguration config;

    @Before
    public void setUp() throws Exception {
        manager = new CachingShopItemManager(mock(DiscountManager.class));
        config = new ShopItemConfiguration(mock(File.class), mock(MTC.class), manager);
//        populateWithExamples(config);
    }

    @Test
    public void testLoadFromString() throws Exception {
        List<ShopItem> itemsBefore = manager.getItems();
        String serialized = config.saveToString();
        config.loadFromString(serialized);
        assertThat("items not same after serialisation as before", manager.getItems(), is(itemsBefore));
    }

//    @Test
//    public void testGetItem() {
//        subTestGetBySerializationName();
//        subTestGetByAliases();
//        subTestGetByDataValues();
//        subTestGetByMaterial();
//        subTestGetByStack();
//    }
//
//    private void subTestGetBySerializationName() {
//        assertThat("Potato not mapped to serialization name", config.getItem(POTATO.getSerializationName()), is(POTATO));
//        assertThat("Diamond not mapped to serialization name", config.getItem(DIAMOND.getSerializationName()), is(DIAMOND));
//    }
//
//    private void subTestGetByMaterial() {
//        assertThat("Potato not mapped to material", config.getItem(Material.POTATO.name()), is(POTATO));
//        assertThat("Diamond mapped to plain material", config.getItem(Material.DIAMOND.name()), is(nullValue()));
//    }

//    @SuppressWarnings("deprecation")
//    private void subTestGetByStack() {
//        assertThat("Potato not mapped to stack",
//                config.getItem(new ItemStack(Material.POTATO)), is(POTATO));
//        assertThat("Diamond wrongly mapped to arbitrary stack",
//                config.getItem(new ItemStack(Material.DIAMOND)), is(nullValue()));
//        assertThat("Diamond not mapped to stack with correct data value",
//                config.getItem(new ItemStack(Material.DIAMOND, 2, (short) 42)), is(DIAMOND));
//    }
//
//    private void subTestGetByAliases() {
//        POTATO.getAliases().forEach(al ->
//                assertThat("Potato not mapped to alias: " + al, config.getItem(al), is(POTATO)));
//        DIAMOND.getAliases().forEach(al ->
//                assertThat("Diamond not mapped to alias: " + al, config.getItem(al), is(DIAMOND)));
//    }
//
//    private void subTestGetByDataValues() {
//        //test that items including all data values (-1) are mapped to all data values
//        assertThat("Potato not mapped to data value 0", config.getItem(Material.POTATO.name() + ":0"), is(POTATO));
//        assertThat("Potato not mapped to data value 1", config.getItem(Material.POTATO.name() + ":1"), is(POTATO));
//        assertThat("Potato not mapped to data value 127", config.getItem(Material.POTATO.name() + ":127"), is(POTATO));
//        assertThat("Potato not mapped to data value 127 (native)", config.getItem(new ItemStack(Material.POTATO, (short) 127)), is(POTATO));
//
//        //reverse case - items with specific data value should only be mapped to that value
//        assertThat("Diamond mapped to data value 0", config.getItem(Material.DIAMOND.name() + ":0"), is(nullValue()));
//        assertThat("Diamond mapped to data value 1", config.getItem(Material.DIAMOND.name() + ":1"), is(nullValue()));
//        assertThat("Diamond mapped to data value 127", config.getItem(Material.DIAMOND.name() + ":127"), is(nullValue()));
//        assertThat("Diamond mapped to data value 127 (native)", config.getItem(new ItemStack(Material.DIAMOND, (short) 127)), is(nullValue()));
//        assertThat("Diamond not mapped to own data value",
//                config.getItem(Material.DIAMOND.name() + ":" + DIAMOND_DATA_VALUE), is(DIAMOND));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testWildcardDataValueException() {
//        //test that the item is not wrongly mapped to -1 (special value)
//        System.out.println(config.getItem(Material.POTATO.name() + ":-1"));
//    }
//
//    //store
//
//    //remove
//
//    private void populateWithExamples(ShopItemConfiguration someConfig) {
//        someConfig.storeItem(DIAMOND);
//        someConfig.storeItem(POTATO);
//    }
}
