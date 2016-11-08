/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import ru.tehkode.permissions.PermissionManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A task that migrates all PEx users who have a unique id stored.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-08
 */
public class KnownIdUserMigrationTask extends AbstractPexImportTask {
    private final int usersPerExecution;
    private final Queue<PexImportUser> workQueue;
    private final LanatusAccountMigrator migrator;
    private final PermissionManager pex;
    private final Collection<PexImportUser> results = new LinkedList<>();

    public KnownIdUserMigrationTask(Collection<PexImportUser> workQueue, int usersPerExecution, LanatusClient lanatus, PermissionManager pex) {
        this.workQueue = new LinkedList<>(workQueue);
        this.usersPerExecution = usersPerExecution;
        migrator = new LanatusAccountMigrator(lanatus);
        migrator.registerMigrationProduct();
        this.pex = pex;
    }

    @Override
    public void run() {
        for (int i = 0; i < usersPerExecution; i++) {
            if (workQueue.isEmpty()) {
                tryCancel();
                getFuture().complete(results);
                return;
            }
            PexImportUser user = workQueue.poll();
            processUser(user);
        }
    }

    private void processUser(PexImportUser user) {
        if (user.hasUniqueId()) {
            migrator.migrateIfNecessary(user.getHandle(), user.getUniqueId());
        } else {
            results.add(user);
        }
    }
}
