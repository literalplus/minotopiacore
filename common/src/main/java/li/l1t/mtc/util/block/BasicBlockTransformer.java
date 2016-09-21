/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.task.NonAsyncBukkitRunnable;
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
    public NonAsyncBukkitRunnable createTransformTask() {
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
