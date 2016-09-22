/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
