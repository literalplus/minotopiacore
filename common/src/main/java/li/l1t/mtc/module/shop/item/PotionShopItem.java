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

package li.l1t.mtc.module.shop.item;

import com.google.common.base.Preconditions;
import li.l1t.common.exception.UserException;
import li.l1t.common.util.PotionHelper;
import li.l1t.common.util.inventory.ItemStackFactory;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an item which can be bought in the MTC admin shop and has additional potion data assigned.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2015-01-19
 */
@SerializableAs("mtc-shop-item-with-potion-effect")
public class PotionShopItem extends AbstractShopItem {
    private static final String POTION_SPEC_PATH = "potion-data";
    private final PotionData data;

    public PotionShopItem(double buyCost, double sellWorth, Material material, List<String> aliases,
                          double discountedPrice, PotionData data) {
        super(material, aliases, sellWorth, buyCost, discountedPrice);
        this.data = Preconditions.checkNotNull(data, "data");
    }

    public PotionShopItem(Map<String, Object> input) {
        super(input);
        this.data = find(String.class, POTION_SPEC_PATH, input)
                .map(PotionHelper::dataFromString)
                .orElse(new PotionData(PotionType.SPEED, false, false));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put(POTION_SPEC_PATH, PotionHelper.stringFromData(data));
        return result;
    }

    public static PotionShopItem fromItemStack(ItemStack stack, String... parameters) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkNotNull(parameters, "parameters");
        PotionData data;
        if(parameters.length == 0) {
            throw new UserException("need at least one argument to specify potion type");
        } else if(parameters.length == 1) {
            data = PotionHelper.dataFromString(parameters[0]);
        } else {
            data = PotionHelper.dataFromString(parameters[0] + ":" + parameters[1]);
        }
        return new PotionShopItem(
                NOT_BUYABLE, NOT_SELLABLE, stack.getType(), new ArrayList<>(), NOT_DISCOUNTABLE, data
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(int amount) {
        return new ItemStackFactory(getMaterial())
                .amount(amount)
                .potion(data)
                .produce();
    }

    @Override
    @Deprecated
    public boolean matches(ItemStack stack) {
        Preconditions.checkNotNull(stack, "stack");
        return getMaterial().equals(stack.getType()) &&
                isMatchingPotion(stack);
    }

    private boolean isMatchingPotion(ItemStack stack) {
        return stack.getItemMeta() instanceof PotionMeta &&
                potionDataMatches((PotionMeta) stack.getItemMeta());
    }

    private boolean potionDataMatches(PotionMeta meta) {
        return meta.getBasePotionData().equals(data);
    }

    public PotionData getPotionData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PotionShopItem)) {
            return false;
        }

        PotionShopItem shopItem = (PotionShopItem) o;

        return getMaterial() == shopItem.getMaterial() && shopItem.data.equals(data);
    }

    @Override
    public int hashCode() {
        int result = getMaterial().hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public String getSerializationName() {
        return getMaterial().name() + ":" + PotionHelper.stringFromData(data);
    }
}
