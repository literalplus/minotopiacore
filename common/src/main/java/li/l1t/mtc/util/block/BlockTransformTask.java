/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import com.google.common.base.Preconditions;
import li.l1t.common.util.task.NonAsyncBukkitRunnable;
import org.bukkit.plugin.Plugin;

/**
 * A task for transforming a large amount of blocks in conjunction with a BlockTransformer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BlockTransformTask extends NonAsyncBukkitRunnable implements TransformTask {
    private final BasicBlockTransformer blockTransformer;
    private Runnable completionCallback = () -> {
    };
    private boolean done = false;

    public BlockTransformTask(BasicBlockTransformer blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public BlockTransformTask withCompletionCallback(Runnable callback) {
        Preconditions.checkNotNull(callback, "callback");
        completionCallback = callback;
        return this;
    }

    @Override
    public void start(Plugin plugin, long delayBetweenExecutions) {
        runTaskTimer(plugin, 0, delayBetweenExecutions);
    }

    @Override
    public void run() {
        done = blockTransformer.continueIteration();
        if (isDone()) {
            completionCallback.run();
            tryCancel();
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }
}
