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

package li.l1t.mtc.module.lanatus.perk;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import li.l1t.common.collections.cache.IdCache;
import li.l1t.common.collections.cache.MapIdCache;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import li.l1t.mtc.module.lanatus.perk.api.PerkFactory;
import li.l1t.mtc.module.lanatus.perk.api.PerkRepository;
import li.l1t.mtc.module.lanatus.perk.perk.StringPerkFactory;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import li.l1t.mtc.module.lanatus.perk.repository.SqlPerkRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages locally enabled perks per player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class LocalPerkManager {
    private final IdCache<UUID, Perk> perkCache = new MapIdCache<>(Perk::getProductId);
    private final Multimap<UUID, Perk> playerPerks = MultimapBuilder.hashKeys().arrayListValues().build();
    private final PerkRepository repository;
    private final PerkFactory factory;

    @InjectMe
    public LocalPerkManager(SqlPerkRepository repository, StringPerkFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public void reapplyAll(Player player) {
        removeAll(player);
        applyEnabled(player);
    }

    public void removeAll(Player player) {
        new ArrayList<>(playerPerks.get(player.getUniqueId()))
                .forEach(perk -> remove(player, perk));
    }

    public void remove(Player player, Perk perk) {
        perk.removeFrom(player);
        playerPerks.remove(player.getUniqueId(), perk);
    }

    public Collection<Perk> applyEnabled(Player player) {
        return repository.findEnabledByPlayerId(player.getUniqueId()).stream()
                .map(this::getPerk)
                .peek(perk -> apply(player, perk))
                .collect(Collectors.toSet());
    }

    public void apply(Player player, Perk perk) {
        perk.applyTo(player);
        playerPerks.put(player.getUniqueId(), perk);
    }

    public Perk getPerk(PerkMeta meta) {
        return perkCache.getOrCompute(meta.getProductId(), ignore -> factory.createPerk(meta));
    }

    public Perk getPerkById(UUID perkId) {
        return perkCache.getOrCompute(perkId, this::createPerkById);
    }

    private Perk createPerkById(UUID perkId) {
        PerkMeta meta = repository.findByProductId(perkId)
                .orElseThrow(() -> new IllegalArgumentException("no perk by that id!"));
        return factory.createPerk(meta);
    }
}
