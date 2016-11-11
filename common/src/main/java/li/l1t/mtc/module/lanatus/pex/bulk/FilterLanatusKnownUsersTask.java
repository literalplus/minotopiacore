/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
                LOGGER.info("Filtering known users: {} users left...", workQueue.size());
            }
            if (workQueue.isEmpty()) {
                tryCancel();
                future.complete(results);
                return;
            }
            PexImportUser user = workQueue.poll();
            if (user.hasUniqueId() && isKnownToLanatus(user)) {
                results.add(user);
            }
        }
    }

    private boolean isKnownToLanatus(PexImportUser user) {
        return lanatus.accounts().find(user.getUniqueId()).isPresent();
    }
}
