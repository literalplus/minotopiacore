package io.github.xxyy.mtc.hook.pex;

import java.util.UUID;

/**
 * A user object from the Permissionsex plugin, storing information about them.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-05
 */
public interface PexUser {
    /**
     * @return the identifier used for this user, for example a name or a unique id
     */
    String getIdentifier();

    /**
     * @return whether this user has a unique id on record
     */
    boolean hasUniqueId();

    /**
     * @return this user's unique id
     */
    UUID getUniqueId();

    /**
     * @return the last known name for this user
     */
    String getName();
}
