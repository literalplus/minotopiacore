/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.misc;

import io.github.xxyy.mtc.MTC;

import java.util.ArrayList;
import java.util.List;

/**
 * Another workaround for clearing caches of instances.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.6.14
 * @see ClearCacheEvent
 */
public final class CacheHelper {
    private CacheHelper() {

    }

    private static List<Cache> CACHES = new ArrayList<>();

    public static void registerCache(Cache cache) {
        CACHES.add(cache);
    }

    public static void clearCaches(boolean forced, MTC plugin) {
        CACHES.stream().forEach(c -> c.clearCache(forced, plugin));
    }

    public interface Cache {
        default void clearCache(boolean forced, MTC plugin) {

        }
    }
}
