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
    private Queue<BlockState> revertQueue = null;

    public BlockRevertTask(RevertableBlockTransformer blockTransformer) {
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
            toRevert.update(true, false);
        }
    }

    private Queue<BlockState> queue() {
        if (revertQueue == null) {
            revertQueue = blockTransformer.getRevertableBlocks();
        }
        return revertQueue;
    }
}
