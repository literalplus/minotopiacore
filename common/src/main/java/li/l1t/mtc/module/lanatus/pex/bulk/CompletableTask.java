/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.bulk;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a task that may be completed and invokes a future upon completion.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-11-07
 */
public interface CompletableTask<T> {
    CompletableFuture<T> getFuture();
}
