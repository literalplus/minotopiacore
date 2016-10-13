/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game.strategy.hardcore;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.InvalidBoardSelectionException;
import li.l1t.mtc.module.putindance.api.game.TickStrategy;
import li.l1t.mtc.module.putindance.game.TickAnnouncer;
import li.l1t.mtc.util.block.BlockPredicates;
import li.l1t.mtc.util.block.BlockTransformers;
import li.l1t.mtc.util.block.RevertableBlockTransformer;
import li.l1t.mtc.util.block.TransformTask;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static li.l1t.common.util.PredicateHelper.not;

/**
 * A tick strategy that announces a single safe colour and removes everything else, just to place it
 * again afterwards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public class TemporaryTickStrategy implements TickStrategy {
    private final Plugin plugin;
    private final long removeDelayTicks;
    private final long revertDelayTicks;
    private final TickAnnouncer tickAnnouncer;
    private TransformTask currentTask;

    public TemporaryTickStrategy(Plugin plugin, long removeDelayTicks, long revertDelayTicks) {
        this.plugin = plugin;
        this.removeDelayTicks = removeDelayTicks;
        this.revertDelayTicks = revertDelayTicks;
        this.tickAnnouncer = new TickAnnouncer(plugin);
    }

    @Override
    public void tick(Game game) {
        Layer layer = game.getBoard().getAllLayers().get(0);
        List<DyeColor> eligibleColors = new ArrayList<>(layer.getActiveColors());
        DyeColor safeColor = selectAndRemoveRandomColorFrom(eligibleColors);
        announceSafeAndRemove(layer, safeColor);
    }

    private DyeColor selectAndRemoveRandomColorFrom(List<DyeColor> eligibleColors) {
        return eligibleColors.remove(RandomUtils.nextInt(eligibleColors.size()));
    }

    private void announceSafeAndRemove(Layer layer, DyeColor safeColor) {
        tickAnnouncer.announceSafeColor(safeColor);
        scheduleBlockRemove(layer, not(BlockPredicates.woolColor(safeColor)));
    }

    private void scheduleBlockRemove(Layer layer, Predicate<Block> filter) {
        RevertableBlockTransformer transformer = BlockTransformers.revertableFiltering()
                .withLocations(layer.getFirstBoundary(), layer.getSecondBoundary())
                .withBlocksPerTick(400)
                .withFilter(filter)
                .withTransformer(block -> block.setType(Material.AIR)).build();
        startRemove(transformer);
    }

    private void startRemove(RevertableBlockTransformer transformer) {
        currentTask = transformer.createTransformTask();
        currentTask.withCompletionCallback(() -> {
            currentTask = transformer.createRevertTask();
            currentTask.withCompletionCallback(() -> currentTask = null)
                    .startDelayed(plugin, revertDelayTicks, 2L);
        });
        currentTask.startDelayed(plugin, removeDelayTicks, 2L);
    }

    @Override
    public boolean isReady() {
        return currentTask == null;
    }

    @Override
    public void checkBoard(XyLocation firstBoundary, XyLocation secondBoundary) throws InvalidBoardSelectionException {
        if (firstBoundary.getBlockY() != secondBoundary.getBlockY()) {
            throw new InvalidBoardSelectionException(this, "Only single-layer selections are supported!");
        }
    }
}
