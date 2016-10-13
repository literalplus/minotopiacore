/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game.strategy.putin;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.game.TickStrategy;
import li.l1t.mtc.module.putindance.game.TickAnnouncer;
import li.l1t.mtc.util.block.BlockPredicates;
import li.l1t.mtc.util.block.BlockTransformers;
import li.l1t.mtc.util.block.FilteringBlockTransformer;
import li.l1t.mtc.util.block.TransformTask;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract base class for tick strategies that permanently remove blocks in a tick on a single
 * layer.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
abstract class AbstractPermanentTickStrategy implements TickStrategy {
    private final Plugin plugin;
    private final long removeDelayTicks;
    private final TickAnnouncer tickAnnouncer;
    private TransformTask removeTask;

    AbstractPermanentTickStrategy(long removeDelayTicks, Plugin plugin) {
        this.removeDelayTicks = removeDelayTicks;
        this.tickAnnouncer = new TickAnnouncer(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean isReady() {
        return removeTask == null;
    }

    protected void announceSafeAndRemove(Layer layer, DyeColor safeColor, DyeColor colorToRemove) {
        tickAnnouncer.announceSafeColor(safeColor);
        scheduleBlockRemove(layer, BlockPredicates.woolColor(colorToRemove));
        layer.removeBlocksByColor(colorToRemove); //some blocks would occasionally be missed, investigate
    }

    protected DyeColor selectAndRemoveRandomColor(List<DyeColor> eligibleColors) {
        return eligibleColors.remove(RandomUtils.nextInt(eligibleColors.size()));
    }

    protected void scheduleBlockRemove(Layer layer, Predicate<Block> filter) {
        FilteringBlockTransformer transformer = BlockTransformers.filtering()
                .withLocations(layer.getFirstBoundary(), layer.getSecondBoundary())
                .withBlocksPerTick(400)
                .withFilter(filter)
                .withTransformer(block -> {
                    layer.removeBlock(block);
                    block.setType(Material.AIR);
                }).build();
        removeTask = transformer.createTransformTask();
        removeTask.withCompletionCallback(() -> removeTask = null)
                .startDelayed(plugin, removeDelayTicks, 2L);

    }

    protected TickAnnouncer announcer() {
        return tickAnnouncer;
    }

    @Override
    public void checkBoard(XyLocation firstBoundary, XyLocation secondBoundary) {
        //we can handle all the things
    }
}
