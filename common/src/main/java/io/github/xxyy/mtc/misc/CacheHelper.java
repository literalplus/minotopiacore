/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc;

import io.github.xxyy.mtc.api.MTCPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Another workaround for clearing caches of instances.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.6.14
 * @see ClearCacheEvent
 */
public final class CacheHelper {
    private static Set<Cache> caches = Collections.newSetFromMap(new WeakHashMap<>());

    private CacheHelper() {

    }

    public static void registerCache(Cache cache) {
        caches.add(cache);
    }

    public static boolean unregisterCache(Cache cache) {
        return caches.remove(cache);
    }

    public static void clearCaches(boolean forced, MTCPlugin plugin) {
        caches.stream().forEach(c -> c.clearCache(forced, plugin));
    }

    public interface Cache {
        default void clearCache(boolean forced, MTCPlugin plugin) {

        }
    }
}
