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
 * Abstract base class for transform tasks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public abstract class AbstractTransformTask extends NonAsyncBukkitRunnable implements TransformTask {
    private Runnable completionCallback = () -> {
    };
    private boolean done = false;

    @Override
    public TransformTask withCompletionCallback(Runnable callback) {
        Preconditions.checkNotNull(callback, "callback");
        completionCallback = callback;
        return this;
    }

    @Override
    public void start(Plugin plugin, long delayBetweenExecutions) {
        startDelayed(plugin, 0, delayBetweenExecutions);
    }

    @Override
    public void startDelayed(Plugin plugin, long delay, long delayBetweenExecutions) {
        runTaskTimer(plugin, delay, delayBetweenExecutions);
    }

    @Override
    public boolean isDone() {
        return done;
    }

    protected void setDoneAndCancel() {
        done = true;
        completionCallback.run();
        tryCancel();
    }
}
