/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board.generator;

import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.generator.ColorSelector;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;

/**
 * A color selector selecting a semi-random color from a list of allowed colors.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public class ListColorSelector implements ColorSelector {
    private final List<DyeColor> validColors = new ArrayList<>(DyeColor.values().length);

    public void addValidColor(DyeColor color) {
        if (!validColors.contains(color)) {
            validColors.add(color);
        }
    }

    public void removeValidColor(DyeColor color) {
        validColors.removeIf(valid -> valid == color);
    }

    public void clearValidColors() {
        validColors.clear();
    }

    @Override
    public DyeColor nextColor(Layer layer) {
        if (validColors.isEmpty()) {
            return DyeColor.BLACK; //help
        }
        return validColors.get(RandomUtils.nextInt(validColors.size()));
    }
}
