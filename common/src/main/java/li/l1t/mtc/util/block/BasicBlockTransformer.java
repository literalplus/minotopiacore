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
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.function.Consumer;

/**
 * Abstract base class for block transformers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BasicBlockTransformer implements BlockTransformer {
    private final World world;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int minX;
    private final int minY;
    private final int minZ;
    private int curX;
    private int curY;
    private int curZ;
    private int maxBlocksPerTick;
    private Consumer<Block> transformer;

    BasicBlockTransformer(XyLocation firstBoundary, XyLocation secondBoundary) {
        Validate.isTrue(firstBoundary.getWorld().equals(secondBoundary.getWorld()), "Both boundaries must be in the same world!");
        Validate.isTrue(!firstBoundary.softEquals(secondBoundary), "Boundaries can't be same block!");
        this.world = firstBoundary.getWorld();
        maxX = Math.max(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        maxY = Math.max(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        maxZ = Math.max(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());
        minX = curX = Math.min(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        minY = curY = Math.min(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        minZ = curZ = Math.min(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());
    }

    @Override
    public BlockTransformTask createTransformTask() {
        return new BlockTransformTask(this);
    }

    /**
     * Continues iterating at the current state of the transformer. Note that execution may be
     * aborted/paused if a certain amount of blocks is processed at once.
     *
     * @return whether the execution is complete and there are no more blocks left
     */
    boolean continueIteration() {
        int processed = 0;
        for (; curX <= maxX; curX++) {
            for (; curZ <= maxZ; curZ++) {
                for (; curY <= maxY; curY++) {
                    Block block = world.getBlockAt(curX, curY, curZ);
                    if (processSingleBlock(block) && ++processed >= maxBlocksPerTick) {
                        curY++; //we just processed that block
                        return false;
                    }
                }
                curY = minY;
            }
            curZ = minZ;
        }
        curX = minX;
        return true;
    }

    /**
     * @param block the block to process
     * @return whether the block was changed
     */
    protected boolean processSingleBlock(Block block) {
        transformer.accept(block);
        return true;
    }

    @Override
    public void setTransformerFunction(Consumer<Block> transformer) {
        this.transformer = transformer;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public int getCurrentXPosition() {
        return curX;
    }

    @Override
    public int getCurrentYPosition() {
        return curY;
    }

    @Override
    public int getCurrentZPosition() {
        return curZ;
    }

    @Override
    public void setBlocksPerTick(int newBlocksPerTick) {
        this.maxBlocksPerTick = newBlocksPerTick;
    }

    @Override
    public int getBlocksPerTick() {
        return maxBlocksPerTick;
    }
}
