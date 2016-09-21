/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import org.bukkit.plugin.Plugin;

/**
 * A task for block transformation that may take a long time. Needs to be explicitly executed.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface TransformTask {
    boolean isDone();

    TransformTask withCompletionCallback(Runnable callback);

    void start(Plugin plugin, long delayBetweenExecutions);
}
