/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.misc.Cache;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Another workaround for clearing caches of instances.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @see ClearCacheEvent
 * @since 22.6.14
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

}
