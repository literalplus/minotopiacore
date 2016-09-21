/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.task.NonAsyncBukkitRunnable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * A block transformer that allows reverting blocks to their initial state. Supports filtering.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BasicRevertableBlockTransformer extends BasicFilteringBlockTransformer implements RevertableBlockTransformer {
    private final Queue<BlockState> transformedBlocks = new LinkedTransferQueue<>();

    BasicRevertableBlockTransformer(XyLocation firstBoundary, XyLocation secondBoundary) {
        super(firstBoundary, secondBoundary);
    }

    @Override
    public Queue<BlockState> getRevertableBlocks() {
        return new LinkedTransferQueue<>(transformedBlocks);
    }

    @Override
    protected boolean processSingleBlock(Block block) {
        BlockState initialState = block.getState();
        if (super.processSingleBlock(block)) {
            transformedBlocks.add(initialState);
            return true;
        }
        return false;
    }

    @Override
    public NonAsyncBukkitRunnable getRevertTask() {
        return new BlockRevertTask(this);
    }
}
