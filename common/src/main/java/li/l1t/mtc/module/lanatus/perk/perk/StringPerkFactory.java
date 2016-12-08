/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.perk;

import com.google.common.base.Preconditions;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.perk.api.*;
import li.l1t.mtc.module.lanatus.perk.perk.nohunger.potion.NoHungerPerkFactory;
import li.l1t.mtc.module.lanatus.perk.perk.potion.PotionPerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates perks based on their type and by passing their data to child factories.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class StringPerkFactory implements CompoundPerkFactory {
    private final Map<String, PerkFactory> factories = new HashMap<>();

    @InjectMe
    public StringPerkFactory(MTCPlugin plugin) {
        registerDefaultFactories(plugin);
    }

    private void registerDefaultFactories(Plugin plugin) {
        registerFactory(PotionPerk.TYPE_NAME, new PotionPerkFactory());
        registerFactory(NoHungerPerk.TYPE_NAME, new NoHungerPerkFactory(plugin));
    }

    @Override
    public void registerFactory(String type, PerkFactory factory) {
        Preconditions.checkNotNull(type, "type");
        Preconditions.checkNotNull(factory, "factory");
        factories.put(type, factory);
    }

    @Override
    public Perk createPerk(PerkMeta meta) {
        Preconditions.checkNotNull(meta, "meta");
        return factories.getOrDefault(meta.getType(), DummyPerkFactory.INSTANCE).createPerk(meta);
    }
}
