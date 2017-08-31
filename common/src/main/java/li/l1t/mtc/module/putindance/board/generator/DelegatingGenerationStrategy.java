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

import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.generator.AirStrategy;
import li.l1t.mtc.module.putindance.api.board.generator.ColorSelector;
import li.l1t.mtc.module.putindance.api.board.generator.GenerationStrategy;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * A generation strategy that randomly places wool and air from a list of allowed colors.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public class DelegatingGenerationStrategy implements GenerationStrategy {
    private final AirStrategy airStrategy;
    private final ColorSelector colorSelector;

    public DelegatingGenerationStrategy(AirStrategy airStrategy, ColorSelector colorSelector) {
        this.airStrategy = airStrategy;
        this.colorSelector = colorSelector;
    }

    @Override
    public void generateAt(Layer layer, Block block) {
        if (airStrategy.shouldGenerateAir(layer, block)) {
            airStrategy.generateAirAt(block);
        } else {
            generateWoolAt(layer, block);
        }
    }

    private void generateWoolAt(Layer layer, Block block) {
        DyeColor color = colorSelector.nextColor(layer);
        applyToBlock(block, color);
        layer.addBlock(block);
    }

    @SuppressWarnings("deprecation")
    private void applyToBlock(Block block, DyeColor color) {
        byte woolData = color.getWoolData();
        block.setTypeIdAndData(Material.WOOL.getId(), woolData, false);
    }
}
