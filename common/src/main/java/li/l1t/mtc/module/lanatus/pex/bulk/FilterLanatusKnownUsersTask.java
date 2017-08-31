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
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

/**
 * A task that removes all users that are known to Lanatus by their unique id from an input
 * collection over multiple iterations. The amount of users to process is defined via a constructor
 * parameter. Note that, to check whether a user is known, the task has to make a database call.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-04
 */
class FilterLanatusKnownUsersTask extends AbstractPexImportTask {
    private static final Logger LOGGER = LogManager.getLogger(FilterLanatusKnownUsersTask.class);
    private final Queue<PexImportUser> workQueue;
    private final int maxUsersPerIteration;
    private final CompletableFuture<Collection<PexImportUser>> future = new CompletableFuture<>();
    private final LanatusClient lanatus;
    private final Queue<PexImportUser> results = new LinkedList<>();

    FilterLanatusKnownUsersTask(Collection<PexImportUser> workQueue, int maxUsersPerIteration, LanatusClient lanatus) {
        this.workQueue = new LinkedList<>(workQueue);
        this.maxUsersPerIteration = maxUsersPerIteration;
        this.lanatus = lanatus;
    }

    public CompletableFuture<Collection<PexImportUser>> getFuture() {
        return future;
    }

    @Override
    public void run() {
        for (int i = 0; i < maxUsersPerIteration; i++) {
            if((workQueue.size() % 50) == 0) {
                LOGGER.info("Filtering known users: Found {}, {} users left...", results.size(), workQueue.size());
            }
            if (workQueue.isEmpty()) {
                tryCancel();
                future.complete(results);
                return;
            }
            PexImportUser user = workQueue.poll();
            if (mightNeedMigration(user)) {
                results.add(user);
            }
        }
    }

    private boolean mightNeedMigration(PexImportUser user) {
        return !user.hasUniqueId() || !isKnownToLanatus(user);
    }

    private boolean isKnownToLanatus(PexImportUser user) {
        return lanatus.accounts().find(user.getUniqueId()).isPresent();
    }
}
