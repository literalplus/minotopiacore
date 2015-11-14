package io.github.xxyy.mtc.hook.pex;

import io.github.xxyy.mtc.hook.MTCHook;

import java.util.Collection;

/**
 * Defines an interface for a hook into the Permissionsex permissioning plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-04
 */
public interface PexHook extends MTCHook {
    Collection<PexGroup> getGroups();
}
