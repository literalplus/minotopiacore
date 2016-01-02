/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

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
