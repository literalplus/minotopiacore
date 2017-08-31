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

import li.l1t.lanatus.api.LanatusClient;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.pex.LanatusAccountMigrator;
import org.apache.logging.log4j.Logger;

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
