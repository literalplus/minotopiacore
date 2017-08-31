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

package li.l1t.mtc.module.putindance.game.strategy.putin;

import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.LayerSelector;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.DyeColor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The default tick strategy for PutinDance. This strategy selects a colour at random and broadcasts
 * its name to all players. Little later, it selects another colour and removes all blocks of the
 * second colour from the current board layer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class PermanentTickStrategy extends AbstractPermanentTickStrategy {
    private final LayerSelector layerSelector;

    public PermanentTickStrategy(Plugin plugin, long removeDelayTicks, LayerSelector layerSelector, int blocksPerTick) {
        super(removeDelayTicks, plugin, blocksPerTick);
        this.layerSelector = layerSelector;
    }

    @Override
    public void tick(Game game) {
        Layer layer = layerSelector.selectLayer(game.getBoard());
        if (layer.getActiveColors().size() == 1) {
            tickFinalColor(game, layer);
        } else {
            tickNormally(layer);
        }
    }

    private void tickFinalColor(Game game, Layer layer) {
        if (isFinalLayer(game)) {
            tickBoardFinalColor(layer);
        } else {
            tickLayerFinalColor(layer);
        }
    }

    private boolean isFinalLayer(Game game) {
        return game.getBoard().getActiveLayerCount() == 1;
    }

    private void tickBoardFinalColor(Layer layer) {
        announcer().announceVodkaMode();
        scheduleBlockRemove(layer, block -> RandomUtils.nextInt(4) == 0);
    }

    private void tickLayerFinalColor(Layer layer) {
        DyeColor finalColor = layer.getActiveColors().stream().findFirst().orElseThrow(AssertionError::new);
        List<DyeColor> eligibleColors = new ArrayList<>(Arrays.asList(DyeColor.values()));
        eligibleColors.remove(finalColor);
        announceSafeAndRemove(layer, eligibleColors.get(RandomUtils.nextInt(eligibleColors.size())), finalColor);
    }

    private void tickNormally(Layer layer) {
        ArrayList<DyeColor> eligibleColors = new ArrayList<>(layer.getActiveColors());
        DyeColor safeColor = selectAndRemoveRandomColor(eligibleColors);
        DyeColor colorToRemove = selectAndRemoveRandomColor(eligibleColors);
        announceSafeAndRemove(layer, safeColor, colorToRemove);
    }

}
