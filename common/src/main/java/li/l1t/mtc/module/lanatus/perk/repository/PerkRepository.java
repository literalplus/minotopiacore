/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.repository;

import li.l1t.common.collections.cache.GuavaMapCache;
import li.l1t.common.collections.cache.MapCache;
import li.l1t.common.collections.cache.OptionalCache;
import li.l1t.common.collections.cache.OptionalGuavaCache;
import li.l1t.common.sql.sane.SaneSql;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.LanatusRepository;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository of perk metadata.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-06
 */
public class PerkRepository implements LanatusRepository {
    public static final String TABLE_NAME = "mt_main.lanatus_perk_product";
    public static final String AVAILABLE_TABLE_NAME = "mt_main.lanatus_perk_available";
    public static final String ENABLED_TABLE_NAME = "mt_main.lanatus_perk_enabled";
    private final OptionalCache<UUID, PerkMeta> idMetaCache = new OptionalGuavaCache<>();
    private final MapCache<UUID, AvailablePerksSet> playerAvailablePerksCache = new GuavaMapCache<>();
    private final MapCache<UUID, Collection<PerkMeta>> playerEnabledPerksCache = new GuavaMapCache<>();
    private final JdbcPerkMetaFetcher perkMetaFetcher;
    private final JdbcAvailablePerksFetcher availablePerksFetcher;
    private final JdbcEnabledPerksFetcher enabledPerksFetcher;
    private final LanatusClient client;

    @InjectMe
    public PerkRepository(MTCLanatusClient client, SaneSql sql) {
        this.client = client;
        this.perkMetaFetcher = new JdbcPerkMetaFetcher(new JdbcPerkMetaCreator(), sql);
        this.availablePerksFetcher = new JdbcAvailablePerksFetcher(new JdbcAvailablePerkCreator(), sql);
        this.enabledPerksFetcher = new JdbcEnabledPerksFetcher(sql);
    }

    public Optional<PerkMeta> findByProductId(UUID productId) {
        return idMetaCache.getOrCompute(productId, perkMetaFetcher::findByProduct);
    }

    public AvailablePerksSet findAvailableByPlayerId(UUID playerId) {
        return playerAvailablePerksCache.getOrCompute(playerId, availablePerksFetcher::findByPlayerId);
    }

    public Collection<PerkMeta> findEnabledByPlayerId(UUID playerId) {
        return playerEnabledPerksCache.getOrCompute(playerId, this::fetchEnabledPerks);
    }

    public boolean isPerkEnabled(UUID playerId, PerkMeta perk) {
        return findEnabledByPlayerId(playerId).contains(perk);
    }

    public boolean isPerkEnabled(UUID playerId, UUID perkId) {
        if(playerEnabledPerksCache.containsKey(playerId)) {
            return playerEnabledPerksCache.get(playerId)
                    .orElseThrow(IllegalStateException::new)
                    .stream()
                    .anyMatch(perk -> perk.getProductId().equals(perkId));
        } else {
            Collection<UUID> perkIds = enabledPerksFetcher.getEnabledPerksByPlayerId(playerId);
            playerEnabledPerksCache.cache(playerId, mapToMetas(perkIds));
            return perkIds.contains(perkId);
        }
    }

    private Collection<PerkMeta> fetchEnabledPerks(UUID playerId) {
        return mapToMetas(enabledPerksFetcher.getEnabledPerksByPlayerId(playerId));
    }

    private Collection<PerkMeta> mapToMetas(Collection<UUID> perkIds) {
        return perkIds.stream()
                .map(this::findByProductId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public void clearCache() {
        idMetaCache.clear();
        playerAvailablePerksCache.clear();
        playerEnabledPerksCache.clear();
    }

    @Override
    public void clearCachesFor(UUID playerId) {
        playerAvailablePerksCache.invalidateKey(playerId);
        playerEnabledPerksCache.invalidateKey(playerId);
    }

    @Override
    public LanatusClient client() {
        return client;
    }
}
