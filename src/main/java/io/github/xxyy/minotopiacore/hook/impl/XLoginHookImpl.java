package io.github.xxyy.minotopiacore.hook.impl;

import io.github.xxyy.minotopiacore.hook.XLoginHook;
import io.github.xxyy.xlogin.common.PreferencesHolder;

import java.util.UUID;

/**
 * Implementation of xLogin hook which contains unsafe statements.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public class XLoginHookImpl {
    public XLoginHookImpl(XLoginHook wrapper) {

    }

    public boolean isAuthenticated(UUID uuid) {
        return PreferencesHolder.getConsumer().getRegistry().isAuthenticated(uuid);
    }
}
