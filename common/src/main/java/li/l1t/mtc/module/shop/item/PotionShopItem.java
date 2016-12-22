/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    private static final String POTION_SPEC_PATH = "potion-effect";
    private final PotionEffect effect;

    public PotionShopItem(double buyCost, double sellWorth, Material material, List<String> aliases,
                          double discountedPrice, PotionEffect effect) {
        super(material, aliases, sellWorth, buyCost, discountedPrice);
        this.effect = Preconditions.checkNotNull(effect, "effect");
    }

    public PotionShopItem(Map<String, Object> input) {
        super(input);
        this.effect = find(String.class, POTION_SPEC_PATH, input)
                .map(PotionHelper::effectFromString)
                .orElse(PotionEffectType.SPEED.createEffect(200, 0));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put(POTION_SPEC_PATH, PotionHelper.stringFromEffect(effect));
        return result;
    }

    public static PotionShopItem fromItemStack(ItemStack stack, String... parameters) {
        Preconditions.checkNotNull(stack, "stack");
        Preconditions.checkNotNull(parameters, "parameters");
        PotionEffect effect;
        if(parameters.length == 0) {
            throw new UserException("need at least one argument to specify potion type");
        } else if(parameters.length == 1) {
            effect = PotionHelper.effectFromString(parameters[0]);
        } else {
            effect = PotionHelper.effectFromString(parameters[0] + ":" + parameters[1]);
        }
        return new PotionShopItem(
                NOT_BUYABLE, NOT_SELLABLE, stack.getType(), new ArrayList<>(), NOT_DISCOUNTABLE, effect
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack toItemStack(int amount) {
        return new ItemStackFactory(getMaterial())
                .amount(amount)
                .effect(effect)
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
                effectsMatch((PotionMeta) stack.getItemMeta());
    }

    private boolean effectsMatch(PotionMeta meta) {
        return meta.getCustomEffects().stream().allMatch(this::effectMatches);
    }

    private boolean effectMatches(PotionEffect effect) {
        return effect.getType() == this.effect.getType() &&
                effect.getAmplifier() == this.effect.getAmplifier() &&
                effect.getDuration() == this.effect.getDuration();
    }

    public PotionEffect getEffect() {
        return effect;
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

        return getMaterial() == shopItem.getMaterial() && effectMatches(shopItem.effect);
    }

    @Override
    public int hashCode() {
        int result = getMaterial().hashCode();
        result = 31 * result + effect.hashCode();
        return result;
    }

    @Override
    public String getSerializationName() {
        return getMaterial().name() + ":" + PotionHelper.stringFromEffect(effect);
    }
}
