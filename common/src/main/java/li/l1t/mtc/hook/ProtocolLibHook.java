/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.hook;

import li.l1t.mtc.hook.impl.ProtocolLibHookImpl;
import org.bukkit.plugin.Plugin;

/**
 * Manages the hook for the ProtocolLib library, which allows to intercept and send packets
 * directly. This hook is only a dummy that acts solely to identify whether the library is available
 * or not.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-04
 */
public class ProtocolLibHook extends SimpleHookWrapper {
    private ProtocolLibHookImpl unsafe;

    public ProtocolLibHook(Plugin plugin) {
        super(plugin);

        unsafe = Hooks.tryHook(this);
    }

    @Override
    public boolean isActive() {
        return unsafe != null && unsafe.isHooked();
    }
}
