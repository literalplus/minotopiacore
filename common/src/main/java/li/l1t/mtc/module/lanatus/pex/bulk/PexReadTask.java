/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
