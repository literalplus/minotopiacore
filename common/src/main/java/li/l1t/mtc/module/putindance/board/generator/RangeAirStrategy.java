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

package li.l1t.mtc.module.putindance.board.generator;

import com.google.common.base.Preconditions;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.generator.AirStrategy;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Determines the probability to generate air by a range of possible values, assigning lower layers
 * more air linearly.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public class RangeAirStrategy implements AirStrategy {
    private int minAirPercent = 40;
    private int maxAirPercent = 70;
    private int rangeWidth = maxAirPercent - minAirPercent;

    public int getMinAirPercent() {
        return minAirPercent;
    }

    public int getMaxAirPercent() {
        return maxAirPercent;
    }

    public void setAirPercentRange(int min, int max) {
        Preconditions.checkArgument(isPercent(min) && min <= max, "required: 0 <= min <= 100 && < max", min, max);
        Preconditions.checkArgument(isPercent(max), "required: 0 <= max", max);
        this.minAirPercent = min;
        this.maxAirPercent = max;
        this.rangeWidth = max - min;
    }

    private boolean isPercent(int toCheck) {
        return toCheck >= 0 && toCheck <= 100;
    }

    @Override
    public boolean shouldGenerateAir(Layer layer, Block block) {
        return RandomUtils.nextInt(100) < airProbability(layer);
    }

    private int airProbability(Layer layer) {
        return minAirPercent + (int) Math.floor(layerHeightFactor(layer) * (double) rangeWidth);
    }

    private double layerHeightFactor(Layer layer) {
        int maxY = layer.getBoard().getMaxYLevel();
        int minY = layer.getBoard().getMinYLevel();
        int layerHeight = layer.getYLevel() - minY;
        int boardHeight = maxY - minY;
        if (boardHeight == 0) {
            return 1D;
        }
        return 1D - (double) (layerHeight / boardHeight);
    }

    @Override
    public void generateAirAt(Block block) {
        if (block.getType() != Material.AIR) {
            block.setType(Material.AIR);
        }
    }
}
