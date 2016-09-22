/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.TickStrategy;
import li.l1t.mtc.util.block.BlockTransformers;
import li.l1t.mtc.util.block.FilteringBlockTransformer;
import li.l1t.mtc.util.block.TransformTask;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * The default tick strategy for PutinDance. This strategy selects a colour at random and broadcasts
 * its name to all players. Little later, it selects another colour and removes all blocks of the
 * second colour from the current board layer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class PutinTickStrategy implements TickStrategy {
    private final Plugin plugin;
    private final long removeDelayTicks;
    private final TickAnnouncer tickAnnouncer;
    private TransformTask removeTask;

    public PutinTickStrategy(Plugin plugin, long removeDelayTicks) {
        this.plugin = plugin;
        this.removeDelayTicks = removeDelayTicks;
        this.tickAnnouncer = new TickAnnouncer(plugin);
    }

    @Override
    public boolean isReady() {
        return removeTask == null;
    }

    @Override
    public void tick(Game game) {
        Layer layer = selectLayer(game);
        if (game.getBoard().getLayerCount() == 1 && layer.getActiveColors().size() == 1) {
            tickVodkaMode(layer);
        } else if (layer.getActiveColors().size() == 1) {
            tickFinalColor(layer);
        } else {
            tickNormally(layer);
        }
    }

    private Layer selectLayer(Game game) {
        Board board = game.getBoard();
        Layer layer = board.getTopMostActiveLayer();
        if (isEligibleForLowerLayerSelection(board, layer)) {
            layer = board.getNextActiveLayerBelow(layer);
        }
        return layer;
    }

    private boolean isEligibleForLowerLayerSelection(Board board, Layer layer) {
        return layer.getActiveColors().size() <= 2 && board.getActiveLayerCount() > 1 && RandomUtils.nextInt(4) == 0;
    }

    private void tickVodkaMode(Layer layer) {
        tickAnnouncer.announceVodkaMode();
        scheduleBlockRemove(layer, block -> RandomUtils.nextInt(4) == 0);
    }

    private void tickFinalColor(Layer layer) {
        DyeColor finalColor = layer.getActiveColors().stream().findFirst().orElseThrow(AssertionError::new);
        List<DyeColor> eligibleColors = new ArrayList<>(Arrays.asList(DyeColor.values()));
        eligibleColors.remove(finalColor);
        tickAnnouncer.announceSafeColor(eligibleColors.get(RandomUtils.nextInt(eligibleColors.size())));
        scheduleBlockRemove(layer, woolColorPredicate(finalColor));
    }

    private void tickNormally(Layer layer) {
        ArrayList<DyeColor> eligibleColors = new ArrayList<>(layer.getActiveColors());
        DyeColor safeColor = selectRandomColor(eligibleColors);
        tickAnnouncer.announceSafeColor(safeColor);
        DyeColor colorToRemove = selectRandomColor(eligibleColors);
        scheduleBlockRemove(layer, woolColorPredicate(colorToRemove));
    }

    private DyeColor selectRandomColor(List<DyeColor> eligibleColors) {
        return eligibleColors.remove(RandomUtils.nextInt(eligibleColors.size()));
    }

    private void scheduleBlockRemove(Layer layer, Predicate<Block> filter) {
        FilteringBlockTransformer transformer = BlockTransformers.filtering()
                .withLocations(layer.getFirstBoundary(), layer.getSecondBoundary())
                .withBlocksPerTick(400)
                .withFilter(filter)
                .withTransformer(block -> {
                    layer.removeBlock(block);
                    block.setType(Material.AIR);
                }).build();
        removeTask = transformer.createTransformTask();
        removeTask.withCompletionCallback(() -> removeTask = null);
        plugin.getServer().getScheduler().runTaskLater(plugin,
                () -> removeTask.start(plugin, 2L),
                removeDelayTicks);

    }

    @SuppressWarnings("deprecation")
    private Predicate<Block> woolColorPredicate(DyeColor expected) {
        return block -> {
            if (block.getType() != Material.WOOL) {
                return false;
            }
            byte rawData = block.getData();
            DyeColor actual = DyeColor.getByWoolData(rawData);
            return actual == expected;
        };
    }
}
