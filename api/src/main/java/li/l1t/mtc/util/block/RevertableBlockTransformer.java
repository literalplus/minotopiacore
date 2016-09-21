/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import li.l1t.common.util.task.NonAsyncBukkitRunnable;
import org.bukkit.block.BlockState;

import java.util.Queue;

/**
 * A block transformer whose actions may be reverted.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface RevertableBlockTransformer extends BlockTransformer {
    Queue<BlockState> getRevertableBlocks();

    NonAsyncBukkitRunnable getRevertTask();
}
