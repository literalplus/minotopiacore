/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.hook.impl;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.xxyy.mtc.hook.HookWrapper;
import io.github.xxyy.mtc.hook.Hooks;

/**
 * Unsafe implementation of the ProtocolLib hook.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public final class ProtocolLibHookImpl implements Hook {
    private boolean hooked = false;

    @Override
    public boolean canHook(HookWrapper wrapper) {
        return Hooks.isPluginLoaded(wrapper, "ProtocolLib");
    }

    @Override
    public void hook(HookWrapper wrapper) {
        hooked = ProtocolLibrary.class.getName() != null; //Make sure the class is loaded
    }

    @Override
    public boolean isHooked() {
        return hooked;
    }
}
