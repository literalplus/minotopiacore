/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import com.google.common.base.Preconditions;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.hook.XLoginHook;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * A task that migrates all PEx users who have a unique id stored.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-08
 */
public class KnownIdUserMigrationTask extends AbstractPexImportTask {
    private static final Logger LOGGER = LogManager.getLogger(KnownIdUserMigrationTask.class);
    private final Collection<PexImportUser> results = new LinkedList<>();
    private final int usersPerExecution;
    private final Queue<PexImportUser> workQueue;
    private final LanatusAccountMigrator migrator;
    private final XLoginProfileImporter importer;

    public KnownIdUserMigrationTask(Collection<PexImportUser> workQueue, int usersPerExecution, LanatusClient lanatus, XLoginProfileImporter importer) {
        this.workQueue = new LinkedList<>(workQueue);
        this.usersPerExecution = usersPerExecution;
        migrator = new LanatusAccountMigrator(lanatus);
        migrator.registerMigrationProduct();
        this.importer = importer;
    }

    @Override
    public void run() {
        for (int i = 0; i < usersPerExecution; i++) {
            if((workQueue.size() % 50) == 0) {
                LOGGER.info("Migrating users with known UUID - {} users left...", workQueue.size());
            }
            if (workQueue.isEmpty()) {
                tryCancel();
                getFuture().complete(results);
                return;
            }
            PexImportUser user = workQueue.poll();
            if(user.getUserName().length() > 16) {
                LOGGER.warn("Username too long: {}", user.getUserName());
                continue;
            }
            processUser(user);
        }
    }

    private void processUser(PexImportUser user) {
        if (user.hasUniqueId()) {
            if(!importer.isKnownToXLogin(user.getUniqueId())) {
                importer.createXLoginProfile(user.getUniqueId(), user.getUserName());
            }
            migrator.migrateIfNecessary(user.getHandle(), user.getUniqueId());
        } else {
            results.add(user);
        }
    }
}
