/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game.strategy.hardcore;

import li.l1t.mtc.util.block.AbstractTransformTask;
import li.l1t.mtc.util.block.RevertableBlockTransformer;
import org.bukkit.block.BlockState;

import java.util.Queue;

/**
 * Reverts block states by setting type id and data instead of updating the state.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public class TypeIdAndDataReverter extends AbstractTransformTask {
    private final RevertableBlockTransformer blockTransformer;
    private Queue<BlockState> revertQueue = null;

    public TypeIdAndDataReverter(RevertableBlockTransformer blockTransformer) {
        this.blockTransformer = blockTransformer;
    }

    @Override
    public void run() {
        for (int i = 0; i < blockTransformer.getBlocksPerTick(); i++) {
            if (queue().isEmpty()) {
                setDoneAndCancel();
                return;
            }
            BlockState toRevert = queue().poll();
            revertTypeIdAndData(toRevert);
        }
    }

    @SuppressWarnings("deprecation")
    private void revertTypeIdAndData(BlockState toRevert) {
        toRevert.getBlock().setTypeIdAndData(toRevert.getTypeId(), toRevert.getRawData(), false);
    }

    private Queue<BlockState> queue() {
        if (revertQueue == null) {
            revertQueue = blockTransformer.getRevertableBlocks();
        }
        return revertQueue;
    }
}
