/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import li.l1t.common.util.task.NonAsyncBukkitRunnable;

/**
 * A task for transforming a large amount of blocks in conjunction with a BlockTransformer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BlockTransformTask extends NonAsyncBukkitRunnable {
    private final BasicBlockTransformer blockTransformer;
    private boolean done = false;

    public BlockTransformTask(BasicBlockTransformer blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public void run() {
        done = blockTransformer.continueIteration();
        if (isDone()) {
            tryCancel();
        }
    }

    public boolean isDone() {
        return done;
    }
}
