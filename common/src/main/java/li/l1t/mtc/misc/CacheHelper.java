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
