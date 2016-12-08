/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.perk.potion;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkFactory;
import li.l1t.mtc.module.lanatus.perk.api.PotionPerk;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates potion perks based on data strings.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PotionPerkFactory implements PerkFactory {
    private static final Pattern SPEC_PATTERN = Pattern.compile("([a-zA-Z_]+)(:[0-9]|)(\\*[0-9]+|)");

    @Override
    public Perk createPerk(PerkMeta meta) {
        Preconditions.checkNotNull(meta, "meta");
        Preconditions.checkArgument(meta.getType().equals(PotionPerk.TYPE_NAME), "expected type potion, was: ", meta);
        return new SimplePotionPerk(meta.getProductId(), createEffectFrom(meta.getData()));
    }

    private PotionEffect createEffectFrom(String spec) {
        Matcher matcher = SPEC_PATTERN.matcher(spec);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Spec does not match expected patttern: " + spec);
        }
        String effectTypeName = matcher.group(1);
        PotionEffectType effectType = PotionEffectType.getByName(effectTypeName);
        Preconditions.checkNotNull(effectType, "unknown effect type : %s", effectTypeName, spec);
        int amplifier = findIntIgnoringFirstCharacter(matcher.group(2)).orElse(0);
        int duration = findIntIgnoringFirstCharacter(matcher.group(3)).orElse(Integer.MAX_VALUE);
        return createEffect(effectType, amplifier, duration);
    }

    private PotionEffect createEffect(PotionEffectType effectType, int amplifier, int duration) {
        return new PotionEffect(effectType, duration, amplifier, true, false, Color.MAROON);
    }

    private Optional<Integer> findIntIgnoringFirstCharacter(String input) {
        if (input.isEmpty()) {
            return Optional.empty();
        } else {
            return findInt(input.substring(1));
        }
    }

    private Optional<Integer> findInt(String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
