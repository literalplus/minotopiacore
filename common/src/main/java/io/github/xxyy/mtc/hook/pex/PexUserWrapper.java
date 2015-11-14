package io.github.xxyy.mtc.hook.pex;

import io.github.xxyy.mtc.logging.LogManager;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Logger;
import ru.tehkode.permissions.PermissionUser;

import java.util.UUID;

/**
 * Wraps a Permissionsex user object.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-14
 */
public class PexUserWrapper implements PexUser {
    private static final Logger LOGGER = LogManager.getLogger(PexUserWrapper.class);
    private final PermissionUser handle;
    private UUID uniqueId; //Can't be final because Java thinks that UUID#fromString() can throw an IAE after the value has been assigned ._.

    public PexUserWrapper(PermissionUser user) {
        this.handle = user;

        try {
            this.uniqueId = UUID.fromString(user.getIdentifier());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid UUID for this user: {}", getIdentifier());
            this.uniqueId = null;
        }
    }

    public PermissionUser getHandle() {
        return handle;
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public String getIdentifier() {
        return handle.getIdentifier();
    }

    @Override
    public boolean hasUniqueId() {
        return uniqueId != null;
    }

    @Override
    public UUID getUniqueId() {
        Validate.notNull(uniqueId, "Invalid UUID for this permission user: " + getIdentifier());
        return uniqueId;
    }
}
