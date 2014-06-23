package io.github.xxyy.minotopiacore.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Another workaround for clearing caches of instances.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.6.14
 * @see io.github.xxyy.minotopiacore.misc.ClearCacheEvent
 */
public final class CacheHelper {
    private CacheHelper() {

    }

    private static List<Cache> CACHES = new ArrayList<>();

    public static void registerCache(Cache cache) {
        CACHES.add(cache);
    }

    public static void clearCaches() {
        CACHES.stream().forEach(Cache::clearCache);
    }

    public interface Cache {
        default void clearCache() {

        }
    }
}
