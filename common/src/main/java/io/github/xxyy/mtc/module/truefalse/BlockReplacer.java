/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.truefalse;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.LocationHelper;
import io.github.xxyy.common.util.task.NonAsyncBukkitRunnable;
import io.github.xxyy.mtc.logging.LogManager;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Replaces huge amounts of blocks with the possibility to divide the workload into multiple steps.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2014-09-04
 */
public class BlockReplacer {
    private static final Logger LOGGER = LogManager.getLogger(BlockReplacer.class);

    private final World world;
    private final int blocksPerExecution;
    private final Predicate<Block> sourceFilter;
    private final Consumer<Block> transformer;
    private final Consumer<BlockState> reverter;

    private final Queue<BlockState> transformedBlocks = new LinkedTransferQueue<>();

    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int minX;
    private final int minY;
    private final int minZ;

    private int curX;
    private int curY;
    private int curZ;

    private TransformTask transformTask;
    private RevertTask revertTask;

    public BlockReplacer(Predicate<Block> sourceFilter, Consumer<Block> transformer, Consumer<BlockState> reverter,
                         XyLocation firstBoundary, XyLocation secondBoundary, int blocksPerExecution) {
        Validate.isTrue(firstBoundary.getWorld().equals(secondBoundary.getWorld()), "Both boundaries must be in the same world!");
        Validate.isTrue(!firstBoundary.softEquals(secondBoundary), "Boundaries can't be same block!");

        this.world = firstBoundary.getWorld();
        this.blocksPerExecution = blocksPerExecution;

        this.sourceFilter = sourceFilter;
        this.transformer = transformer;
        this.reverter = reverter;

        maxX = Math.max(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        maxY = Math.max(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        maxZ = Math.max(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());

        minX = curX = Math.min(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        minY = curY = Math.min(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        minZ = curZ = Math.min(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());
    }

    /**
     * Static utility method for reverting a block to a previous state. This method works by forcing an update on the
     * previous state, changing the block (back) to it.
     *
     * @param previousState the previous state to revert the corresponding block to
     */
    public static void defaultReverter(BlockState previousState) {
        if (!previousState.update(true, false)) {
            previousState.getBlock().setType(previousState.getType(), false);
            LOGGER.warn("Could not revert %s at %s: Update failed!",
                    previousState.getType(), LocationHelper.prettyPrint(previousState.getLocation()));
        }
    }

    public void scheduleTransform(Plugin plugin) {
        Validate.isTrue(transformTask == null, "Transform already scheduled!");

        transformTask = new TransformTask();
        transformTask.runTaskTimer(plugin, 2L, 2L);
    }

    public boolean canRevert() {
        return transformTask != null && transformTask.isDone();
    }

    public void scheduleRevert(Plugin plugin) {
        Validate.isTrue(canRevert(), "Cannot revert right now!");

        transformTask = null;
        revertTask = new RevertTask();
        revertTask.runTaskTimer(plugin, 2L, 2L);
    }

    public boolean isReverted() {
        return revertTask != null && revertTask.isDone();
    }

    private class TransformTask extends NonAsyncBukkitRunnable {
        private boolean done = false;

        @Override
        public void run() {
            int processed = 0;

            for (; curX <= maxX; curX++) {
                for (; curZ <= maxZ; curZ++) {
                    for (; curY <= maxY; curY++) {
                        Block block = world.getBlockAt(curX, curY, curZ);
                        if (sourceFilter.test(block)) {
                            transformer.accept(block);
                            transformedBlocks.add(block.getState());

                            if (++processed >= blocksPerExecution) {
                                return; //Done for now, wait for next execution
                            }
                        }
                    }
                    curY = minY;
                }
                curZ = minZ;
            }
            curX = minX;

            done = true;
            tryCancel();
        }

        public boolean isDone() {
            return done;
        }
    }

    private class RevertTask extends NonAsyncBukkitRunnable {
        @Override
        public void run() {
            for (int processed = 0; processed < blocksPerExecution && !transformedBlocks.isEmpty(); processed++) {
                reverter.accept(transformedBlocks.poll());
            }

            if (isDone()) {
                tryCancel();
            }
        }

        public boolean isDone() {
            return transformedBlocks.isEmpty();
        }
    }
}
