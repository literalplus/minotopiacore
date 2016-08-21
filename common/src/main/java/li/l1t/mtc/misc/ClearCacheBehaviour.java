/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.misc;

/**
 * Lists standard behaviours of configuration files upon cache clear
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19/01/15
 */
public enum ClearCacheBehaviour {
    /**
     * Reloads the config file upon any cache clear.
     */
    RELOAD,
    /**
     * Saves the config file upon any cache clear.
     */
    SAVE,
    /**
     * Saves the config file for normal cache clears and reloads it for forced ones.
     */
    RELOAD_ON_FORCED,
    /**
     * Does nothing on cache clear.
     */
    NOTHING
}
