/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.util.UUIDHelper;
import ru.tehkode.permissions.PermissionUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Represents a PermissionsEx user to be imported into Lanatus in memory.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-04
 */
class PexImportUser {
    private final String userName;
    private final UUID uniqueId;
    private final PermissionUser user;

    private PexImportUser(String userName, UUID uniqueId, PermissionUser user) {
        this.userName = userName;
        this.uniqueId = uniqueId;
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean hasUniqueId() {
        return uniqueId != null;
    }

    public PermissionUser getHandle() {
        return user;
    }

    public static PexImportUser of(PermissionUser user) {
        return new PexImportUser(
                user.getName(), findUniqueId(user).orElse(null),
                user
        );
    }

    private static Optional<UUID> findUniqueId(PermissionUser user) {
        if (UUIDHelper.isValidUUID(user.getIdentifier())) {
            return Optional.of(UUIDHelper.getFromString(user.getIdentifier()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.format("{PexImportUser: %s|%s}", userName, uniqueId);
    }
}
