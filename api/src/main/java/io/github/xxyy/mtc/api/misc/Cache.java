package io.github.xxyy.mtc.api.misc;

import io.github.xxyy.mtc.api.MTCPlugin;

/**
 * Something that has some kind of clearable cache.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-12-31
 */
public interface Cache {

    /**
     * Clears this cache's cache.
     *
     * @param forced whether this cache clear was forced
     * @param plugin the plugin inducing this cache clear
     */
    default void clearCache(boolean forced, MTCPlugin plugin) {

    }
}
