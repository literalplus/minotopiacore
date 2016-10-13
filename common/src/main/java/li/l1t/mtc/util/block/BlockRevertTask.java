/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import org.bukkit.block.BlockState;

import java.util.Queue;

/**
 * A task for reverting a large amount of blocks using a revertable block transformer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BlockRevertTask extends AbstractTransformTask {
    private final RevertableBlockTransformer blockTransformer;

    public BlockRevertTask(RevertableBlockTransformer blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public void run() {
        Queue<BlockState> queue = blockTransformer.getRevertableBlocks();
        for (int i = 0; i < blockTransformer.getBlocksPerTick(); i++) {
            if (queue.isEmpty()) {
                setDoneAndCancel();
                return;
            }
            BlockState toRevert = queue.poll();
            toRevert.update(true, false);
        }
    }
}
