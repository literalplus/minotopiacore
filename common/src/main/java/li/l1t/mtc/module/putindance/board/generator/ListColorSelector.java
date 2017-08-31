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
