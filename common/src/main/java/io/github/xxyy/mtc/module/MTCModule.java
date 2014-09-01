package io.github.xxyy.mtc.module;

import io.github.xxyy.mtc.MTC;
import io.github.xxyy.mtc.misc.CacheHelper;

/**
 * Represents a MTC module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public interface MTCModule extends CacheHelper.Cache {
    void enable(MTC plugin);
    void disable(MTC plugin);
    void reload(MTC plugin);

    String getName();
    boolean isEnabled(MTC plugin);
}
