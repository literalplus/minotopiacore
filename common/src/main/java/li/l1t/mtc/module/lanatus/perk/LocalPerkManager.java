/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
