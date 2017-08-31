/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
