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

package li.l1t.mtc.util.block;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.function.Predicate;

/**
 * Static utility class providing predicates for filtering of blocks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public class BlockPredicates {
    private BlockPredicates() {
    }

    /**
     * @param expected the color to match
     * @return a predicate that matches only blocks of given wool color
     */
    public static Predicate<Block> woolColor(DyeColor expected) {
        return block -> findWoolColor(block) == expected;
    }

    @SuppressWarnings("deprecation")
    private static DyeColor findWoolColor(Block block) {
        if (block.getType() != Material.WOOL) {
            return null;
        }
        byte rawData = block.getData();
        return DyeColor.getByWoolData(rawData);
    }
}
