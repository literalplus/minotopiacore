package io.github.xxyy.minotopiacore.hook.impl;

import io.github.xxyy.minotopiacore.hook.HookWrapper;

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
