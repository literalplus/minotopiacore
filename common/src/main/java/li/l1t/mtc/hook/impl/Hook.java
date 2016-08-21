/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.hook.impl;

import li.l1t.mtc.hook.HookWrapper;

/**
 * A hook to hook into stuff, for example other pirate ships..um..I meant plugins
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.8.14
 */
public interface Hook {
    boolean canHook(HookWrapper wrapper);

    void hook(HookWrapper wrapper);

    boolean isHooked();
}
