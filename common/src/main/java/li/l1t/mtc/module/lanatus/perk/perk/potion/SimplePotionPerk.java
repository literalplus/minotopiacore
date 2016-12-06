/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.perk.potion;

import li.l1t.mtc.module.lanatus.perk.perk.AbstractPerk;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

/**
 * Represents a perk that provides a potion effect.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class SimplePotionPerk extends AbstractPerk {
    private final PotionEffect effect;

    public SimplePotionPerk(UUID productId, PotionEffect effect) {
        super(productId);
        this.effect = effect;
    }

    @Override
    public void applyTo(Player player) {
        player.addPotionEffect(effect, true);
    }

    @Override
    public void removeFrom(Player player) {
        player.removePotionEffect(effect.getType());
    }
}
