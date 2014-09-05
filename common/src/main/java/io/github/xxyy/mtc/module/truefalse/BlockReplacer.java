package io.github.xxyy.mtc.module.truefalse;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import io.github.xxyy.common.misc.XyLocation;
import io.github.xxyy.common.util.task.NonAsyncBukkitRunnable;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Class for replacing lots of blocks.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 4.9.14
 */
public class BlockReplacer {

    private final XyLocation firstBoundary;
    private final XyLocation secondBoundary;
    private final World world;
    private final int blocksPerExecution;
    private final Predicate<Block> sourceFilter;
    private final Consumer<Block> transformer;
    private final Consumer<Block> reverter;

    private final Queue<Block> transformedBlocks = new LinkedTransferQueue<>();

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private int curX;
    private int curY;
    private int curZ;

    private TransformTask transformTask;
    private RevertTask revertTask;

    public BlockReplacer(Predicate<Block> sourceFilter, Consumer<Block> transformer, Consumer<Block> reverter,
                         XyLocation firstBoundary, XyLocation secondBoundary, int blocksPerExecution) {
        Validate.isTrue(firstBoundary.getWorld().equals(secondBoundary.getWorld()), "Both boundaries must be in the same world!");
        Validate.isTrue(!firstBoundary.softEquals(secondBoundary), "Boundaries can't be same block!");

        this.firstBoundary = firstBoundary;
        this.secondBoundary = secondBoundary;
        this.world = firstBoundary.getWorld();
        this.blocksPerExecution = blocksPerExecution;

        this.sourceFilter = sourceFilter;
        this.transformer = transformer;
        this.reverter = reverter;

        maxX = Math.max(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        maxY = Math.max(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        maxZ = Math.max(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());

        curX = Math.min(firstBoundary.getBlockX(), secondBoundary.getBlockX());
        curY = Math.min(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        curZ = Math.min(firstBoundary.getBlockZ(), secondBoundary.getBlockZ());
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
                            transformedBlocks.add(block);

                            if (++processed >= blocksPerExecution) {
                                return; //Done for now, wait for next execution
                            }
                        }
                    }
                }
            }

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
            for(int processed = 0; processed < blocksPerExecution && !transformedBlocks.isEmpty(); processed++) {
                reverter.accept(transformedBlocks.poll());
            }

            if(isDone()) {
                tryCancel();
            }
        }

        public boolean isDone() {
            return transformedBlocks.isEmpty();
        }
    }
}
