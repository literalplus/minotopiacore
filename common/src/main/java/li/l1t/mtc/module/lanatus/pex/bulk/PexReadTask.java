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

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A task that imports existing user data from PermissionsEx into memory for further processing.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-04
 */
class PexReadTask extends AbstractPexImportTask {
    private final List<PexImportUser> resultDeque = new LinkedList<>();
    private final PermissionManager pex;
    private final Predicate<PermissionUser> userFilter;

    PexReadTask(PermissionManager pex, Predicate<PermissionUser> userFilter) {
        this.pex = pex;
        this.userFilter = userFilter;
    }

    @Override
    public void run() {
        Set<PermissionUser> allKnownPexUsers = pex.getUsers();
        allKnownPexUsers.stream()
                .filter(userFilter)
                .forEach(this::readPexUser);
        tryCancel();
        getFuture().complete(resultDeque);
    }

    private void readPexUser(PermissionUser user) {
        resultDeque.add(PexImportUser.of(user));
    }
}
