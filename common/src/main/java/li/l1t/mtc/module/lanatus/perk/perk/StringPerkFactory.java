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
