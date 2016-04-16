/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.shop;

import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 * Tests data validation and miscellaneous methods in ShopItem.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-03-27
 */
@SuppressWarnings("deprecation")
public class ShopItemTest {

    @Test
    public void testConstructorValidation() throws Exception {

    }

    @Test
    public void testToItemStack() throws Exception {
        ItemStack sourceStack = new ItemStack(Material.COAL_BLOCK, 12);
        sourceStack.getData().setData((byte) 42);
        ShopItem item = createItemFromStack(sourceStack);
        ItemStack generatedStack = item.toItemStack(12);
        assertThat("toItemStack() item stack does not match source stack", generatedStack, is(sourceStack));
    }

    @Test
    public void testToItemStack__wildcard() throws Exception {
        ItemStack sourceStack = new ItemStack(Material.COAL_BLOCK, 12);
        ShopItem item = createItemFromMaterialAndData(Material.COAL_BLOCK, ShopItem.WILDCARD_DATA_VALUE);
        ItemStack generatedStack = item.toItemStack(12);
        assertThat("toItemStack() generates items with data -1", generatedStack.getData().getData(), is(not(-1)));
        assertThat("toItemStack() item stack does not match source stack", generatedStack, is(sourceStack));
    }

    @Test
    @Ignore
    public void testMatches() throws Exception {
        //deprecated method
    }

    @Test
    public void testCanBeSold() throws Exception {
        ShopItem unsellableItem = createItemWithPrices(42D, ShopItem.NOT_SELLABLE);
        assumeThat(unsellableItem.getSellWorth(), is(ShopItem.NOT_SELLABLE));
        assertThat("canBeSold malfunctioning", unsellableItem.canBeSold(), is(false));

        ShopItem sellableItem = createItemWithPrices(ShopItem.NOT_BUYABLE, 56D);
        assumeThat(sellableItem.getSellWorth(), is(56D));
        assertThat("canBeSold malfunctioning", sellableItem.canBeSold(), is(true));
    }

    @Test
    public void testCanBeBought() throws Exception {
        ShopItem notBuyableItem = createItemWithPrices(ShopItem.NOT_BUYABLE, 87D);
        assumeThat(notBuyableItem.getBuyCost(), is(ShopItem.NOT_BUYABLE));
        assertThat("canBeBought malfunctioning", notBuyableItem.canBeBought(), is(false));

        ShopItem buyableItem = createItemWithPrices(1337D, ShopItem.NOT_SELLABLE);
        assumeThat(buyableItem.getBuyCost(), is(1337D));
        assertThat("canBeBought malfunctioning", buyableItem.canBeBought(), is(true));
    }

    @Test
    public void testAddAlias() throws Exception {
        ShopItem item = createAnyItem();
        assumeThat(item.getAliases(), is(notNullValue()));
        assumeThat(item.getAliases().size(), is(0));

        String someAlias = "my alias";
        item.addAlias(someAlias);
        assertThat(item.getAliases(), hasItem(someAlias));
    }

    @Test
    public void testRemoveAlias() throws Exception {
        ShopItem item = createAnyItem();
        assumeThat(item.getAliases(), is(notNullValue()));
        assumeThat(item.getAliases().size(), is(0));

        String someAlias = "my alias";
        item.addAlias(someAlias);
        assumeThat(item.getAliases(), hasItem(someAlias));

        item.removeAlias(someAlias);
        assertThat("remove not working", item.getAliases(), not(hasItem(someAlias)));
    }

    @Test
    public void testRemoveAlias1() throws Exception {
        ShopItem item = createAnyItem();
        assumeThat(item.getAliases(), is(notNullValue()));
        assumeThat(item.getAliases().size(), is(0));

        String someAlias = "my alias";
        item.addAlias(someAlias);
        assumeThat(item.getAliases().get(0), is(someAlias));

        item.removeAlias(0);
        assertThat("remove by id not working", item.getAliases(), not(hasItem(someAlias)));
    }

    @Test
    public void testSetDisplayName() throws Exception {
        ShopItem item = createAnyItem();
        assumeThat(item.getAliases(), is(notNullValue()));
        assumeThat(item.getAliases().size(), is(0));

        String someAlias = "some alias";
        item.addAlias(someAlias);
        String displayName = "my display name";
        item.setDisplayName(displayName);
        assertThat("display name not set", item.getDisplayName(), is(displayName));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBuyCost__validation__sellWorth() throws Exception {
        ShopItem item = createAnyItem();
        item.setSellWorth(42D);
        item.setBuyCost(12D); //must not be less than sell worth, free money otherwise
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSellWorth__validation__discountedPrice() throws Exception {
        ShopItem item = createAnyItem();
        item.setBuyCost(12D);
        item.setDiscountedPrice(10D);
        item.setSellWorth(11D); //must not be greater than discounted price, free money otherwise
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSellWorth__validation__buyCost() throws Exception {
        ShopItem item = createAnyItem();
        item.setBuyCost(12D);
        item.setSellWorth(15D); //must not be greater than buy cost, free money otherwise
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountedPrice__validation__buyCost() throws Exception {
        ShopItem item = createAnyItem();
        item.setBuyCost(12D);
        item.setDiscountedPrice(15D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiscountedPrice__validation__sellWorth() throws Exception {
        ShopItem item = createAnyItem();
        item.setSellWorth(17D);
        item.setDiscountedPrice(15D);
    }

    @Test
    public void testGetDiscountPercentage() throws Exception {
        ShopItem item = createAnyItem();
        item.setBuyCost(10D);
        item.setDiscountedPrice(6D);
        assertThat("miscalculated discount percentage", item.getDiscountPercentage(), is(40D));
        item.setDiscountedPrice(1D); //OMG ONE DIRECTION!!!!!!1
        assertThat("miscalculated discount percentage", item.getDiscountPercentage(), is(90D));
    }

    @Test
    public void testIsDiscountable() throws Exception {
        ShopItem item = createAnyItem();
        item.setBuyCost(10D);
        assertThat("isDiscountable", item.isDiscountable(), is(false));
        item.setDiscountedPrice(5D);
        assertThat("isDiscountable", item.isDiscountable(), is(true));
    }

    @Test
    public void testGetSerializationName__wildcard() throws Exception {
        ShopItem item = createItemFromMaterialAndData(Material.MINECART, ShopItem.WILDCARD_DATA_VALUE);
        assertThat(item.getSerializationName(), is("MINECART"));
    }

    @Test
    public void testGetSerializationName__dataValue() throws Exception {
        ShopItem item = createItemFromMaterialAndData(Material.ACACIA_DOOR, (byte) 42);
        assertThat(item.getSerializationName(), is("ACACIA_DOOR:42"));
    }

    @Test
    public void serializeToSection__basic() throws Exception {
        ShopItem item = createItemFromMaterialAndData(Material.CAKE, ShopItem.WILDCARD_DATA_VALUE);
        item.setBuyCost(15D);
        item.setSellWorth(10D);
        MemoryConfiguration configuration = new MemoryConfiguration();
        item.serializeToSection(configuration);
        assertThat("configuration did not contain item", configuration.contains(item.getSerializationName()), is(true));
        ShopItem deserializedItem = ShopItem.deserialize(configuration, null);
        assertThat("deserialized item not equal to initial", deserializedItem, is(equalTo(item)));
    }

    @Test
    public void serializeToSection__discounted() throws Exception {
        ShopItem item = createItemFromMaterialAndData(Material.CAKE, ShopItem.WILDCARD_DATA_VALUE);
        item.setBuyCost(15D);
        item.setSellWorth(10D);
        item.setDiscountedPrice(12D);
        MemoryConfiguration configuration = new MemoryConfiguration();
        item.serializeToSection(configuration);
        assertThat("configuration did not contain item", configuration.contains(item.getSerializationName()), is(true));
        ShopItem deserializedItem = ShopItem.deserialize(configuration, null);
        assertThat("deserialized item not equal to initial", deserializedItem, is(equalTo(item)));
    }

    @Nonnull
    private ShopItem createAnyItem() {
        return createItemWithPrices(ShopItem.NOT_BUYABLE, ShopItem.NOT_SELLABLE);
    }

    @Nonnull
    private ShopItem createItemFromStack(ItemStack stack) {
        return createItemFromMaterialAndData(stack.getType(), stack.getData().getData());
    }

    @Nonnull
    private ShopItem createItemFromMaterialAndData(Material type, byte dataValue) {
        return new ShopItem(null, ShopItem.NOT_BUYABLE, ShopItem.NOT_SELLABLE,
                type, dataValue,
                new ArrayList<>(), ShopItem.NOT_DISCOUNTABLE);
    }

    @Nonnull
    private ShopItem createItemWithPrices(double buyCost, double sellWorth) {
        return createItemWithPricesAndDiscount(buyCost, sellWorth, ShopItem.NOT_DISCOUNTABLE);
    }

    @Nonnull
    private ShopItem createItemWithPricesAndDiscount(double buyCost, double sellWorth, double discountedPrice) {
        return new ShopItem(null, buyCost, sellWorth,
                Material.AIR, ShopItem.WILDCARD_DATA_VALUE,
                new ArrayList<>(), discountedPrice);
    }
}
