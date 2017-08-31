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

import li.l1t.common.misc.XyLocation;
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
    public BlockRevertTask createRevertTask() {
        return new BlockRevertTask(this);
    }
}
