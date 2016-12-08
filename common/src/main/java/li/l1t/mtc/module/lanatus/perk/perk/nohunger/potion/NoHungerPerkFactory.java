/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.perk.nohunger.potion;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.bukkit.plugin.Plugin;

/**
 * Creates potion perks based on data strings.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class NoHungerPerkFactory implements PerkFactory {
    private final Plugin plugin;

    public NoHungerPerkFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Perk createPerk(PerkMeta meta) {
        Preconditions.checkNotNull(meta, "meta");
        Preconditions.checkArgument(meta.getType().equals("nohunger"), "expected type nohunger, was: ", meta);
        return new MetadataNoHungerPerk(meta.getProductId(), plugin);
    }
}
