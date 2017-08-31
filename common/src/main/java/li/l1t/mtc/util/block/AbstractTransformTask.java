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
