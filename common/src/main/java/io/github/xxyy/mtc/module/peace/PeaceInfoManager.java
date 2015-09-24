package io.github.xxyy.mtc.module.peace;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class PeaceInfoManager {

    @NotNull
    private final PeaceModule module;
    private boolean disableFlush = false;

    private final LoadingCache<UUID, PeaceInfo> peaceInfoCache = CacheBuilder.newBuilder()
            .initialCapacity(5)
            .weakValues()
            .concurrencyLevel(2)
            .removalListener(new RemovalListener<UUID, PeaceInfo>() {
                @Override
                public void onRemoval(@NotNull RemovalNotification<UUID, PeaceInfo> notification) {
                    if (disableFlush) {
                        return;
                    }
                    switch (notification.getCause()) {
                        case COLLECTED:
                        case EXPIRED:
                        case EXPLICIT:
                        case SIZE: {
                            module.getPlugin().getServer().getScheduler().runTaskAsynchronously(module.getPlugin(),
                                    () -> flush(notification.getValue()));
                            break;
                        }
                    }
                }
            })
            .build(CacheLoader.from(this::fetch));

    public PeaceInfoManager(@NotNull PeaceModule module) {
        this.module = module;
    }

    /**
     * If you are planning to use {@link #get(UUID)} after this method, consider using {@link #getIfCached(UUID)}, as its faster
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return whether the {@link PeaceInfo} of the given uuid is cached currently
     */
    public boolean isCached(UUID uuid) {
        return peaceInfoCache.getIfPresent(uuid) != null;
    }

    /**
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the cache {@link PeaceInfo} or null if its not cached
     */
    @Nullable
    public PeaceInfo getIfCached(UUID uuid) {
        return peaceInfoCache.getIfPresent(uuid);
    }

    /**
     * Gets either the cached value or performes a database lookup
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the PeaceInfo
     */
    public PeaceInfo get(UUID uuid) {
        return peaceInfoCache.getUnchecked(uuid);
    }

    /**
     * Gets either the cached value or performes a database lookup and then removes it from the cache.
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the PeaceInfo
     */
    public PeaceInfo getAndFlush(UUID uuid) { //TODO needed?
        PeaceInfo peaceInfo = get(uuid);
        flush(uuid);
        return peaceInfo;
    }

    /**
     * Removes the {@link PeaceInfo} from the cache and saves it asynchroniously to the database
     * @param uuid the uuid which {@link PeaceInfo} should be removed
     */
    public void flush(UUID uuid) {
        peaceInfoCache.invalidate(uuid);
    }

    /**
     * Removes the {@link PeaceInfo} from the cache and does not save it
     * @param uuid the uuid which {@link PeaceInfo} should be removed
     */
    public void discard(UUID uuid) {
        disableFlush = true;
        peaceInfoCache.invalidate(uuid);
        disableFlush = false;
    }

    /**
     * Clears the {@link PeaceInfo} cache and saves it asynchroniously to the database
     */
    public void flushAll() {
        peaceInfoCache.invalidateAll();
    }

    /**
     * Clears the {@link PeaceInfo} cache and does not save it
     */
    public void discardAll() {
        disableFlush = true;
        peaceInfoCache.invalidateAll();
        disableFlush = false;
    }

    private PeaceInfo fetch(UUID uuid) { //TODO implement
        return null;
    }

    private void flush(PeaceInfo value) { //TODO implement
    }

    private PeaceInfo createNew(UUID uuid) {
        return new PeaceInfo(uuid, new ArrayList<>());
    }

}
