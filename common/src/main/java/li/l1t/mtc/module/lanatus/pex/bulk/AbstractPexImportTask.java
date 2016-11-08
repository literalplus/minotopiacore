/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import li.l1t.common.util.task.ImprovedBukkitRunnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for completable tasks that provide a future of a collection of import users.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-08
 */
public abstract class AbstractPexImportTask extends ImprovedBukkitRunnable implements CompletableTask<Collection<PexImportUser>> {
    private final CompletableFuture<Collection<PexImportUser>> future = new CompletableFuture<>();

    @Override
    public CompletableFuture<Collection<PexImportUser>> getFuture() {
        return future;
    }
}
