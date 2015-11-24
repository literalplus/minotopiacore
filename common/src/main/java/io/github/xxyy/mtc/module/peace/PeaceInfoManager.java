package io.github.xxyy.mtc.module.peace;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class PeaceInfoManager {

    private static final int MAXIMUM_CACHE_SIZE = 200;

    @NotNull
    private final PeaceModule module;
    private final ExecutorService asyncSaverThread = Executors
        .newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                .setNameFormat("PeaceInfo Save Thread #%d")
                .build());
    private boolean disableFlush = false;

    private final LoadingCache<UUID, PeaceInfo> peaceInfoCache = CacheBuilder.newBuilder()
        .initialCapacity(30)
        .concurrencyLevel(2)
        .maximumSize(MAXIMUM_CACHE_SIZE)
        .removalListener(new RemovalListener<UUID, PeaceInfo>() {
            @Override
            public void onRemoval(@NotNull RemovalNotification<UUID, PeaceInfo> notification) {
                if (disableFlush) {
                    return;
                }
                switch (notification.getCause()) {
                    case EXPIRED:
                    case COLLECTED: //<- should not happen, but to be sure
                    case EXPLICIT:
                    case SIZE: {
                        PeaceInfo value = notification.getValue();
                        if (value != null && value.isDirty()) {
                            asyncSaverThread.execute(new FlushRunnable(value));
                        }
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
     * Waits until all save tasks are stopped or 10 seconds elapsed
     * (if really 10 seconds elapse, there is an error somewhere)
     */
    void syncStop() {
        try {
            asyncSaverThread.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * If you are planning to use {@link #get(UUID)} after this method, consider using {@link #getIfCached(UUID)}, as its faster
     *
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
     *
     * @param uuid the uuid to get the {@link PeaceInfo} from.
     * @return the PeaceInfo
     */
    public PeaceInfo get(UUID uuid) {
        return peaceInfoCache.getUnchecked(uuid);
    }

    /**
     * Gets either the cached value or performes a database lookup and then removes it from the cache.
     *
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
     *
     * @param uuid the uuid which {@link PeaceInfo} should be removed
     */
    public void flush(UUID uuid) {
        peaceInfoCache.invalidate(uuid);
        //TODO save async
    }

    /**
     * Removes the {@link PeaceInfo} from the cache and does not save it
     *
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

    private static PeaceInfo createNew(UUID uuid) {
        return new PeaceInfo(uuid, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private final class FlushRunnable implements Runnable {

        @NotNull
        private final PeaceInfo peaceInfo;

        private FlushRunnable(@NotNull PeaceInfo peaceInfo) {
            this.peaceInfo = peaceInfo;
        }

        @Override
        public void run() {
            if (!peaceInfo.isDirty()) {
                return;
            }
            //TODO save peaceInfo
        }
    }

    @NotNull
    public PeaceModule getModule() {
        return module;
    }
}
