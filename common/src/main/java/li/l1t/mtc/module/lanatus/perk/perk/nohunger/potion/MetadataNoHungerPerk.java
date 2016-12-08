/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.perk.nohunger.potion;

import li.l1t.mtc.module.lanatus.perk.perk.AbstractPerk;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Represents a perk that provides a potion effect.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class MetadataNoHungerPerk extends AbstractPerk implements li.l1t.mtc.module.lanatus.perk.api.NoHungerPerk {
    private final Plugin plugin;

    public MetadataNoHungerPerk(UUID productId, Plugin plugin) {
        super(productId);
        this.plugin = plugin;
    }

    @Override
    public void applyTo(Player player) {
        player.setMetadata(TYPE_NAME, new FixedMetadataValue(plugin, ""));
    }

    @Override
    public void removeFrom(Player player) {
        player.removeMetadata(TYPE_NAME, plugin);
    }
}
