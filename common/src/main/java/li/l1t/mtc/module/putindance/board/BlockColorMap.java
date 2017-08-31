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

package li.l1t.mtc.module.putindance.board;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;

import java.util.*;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class BlockColorMap {
    private final Multimap<DyeColor, Block> blocksByColor = MultimapBuilder
            .hashKeys(DyeColor.values().length).arrayListValues().build();
    private final Map<Block, DyeColor> colorByBlock = new HashMap<>();

    public void put(DyeColor color, Block block) {
        blocksByColor.put(color, block);
        colorByBlock.put(block, color);
    }

    public DyeColor getColor(Block block) {
        return colorByBlock.get(block);
    }

    public Collection<Block> getBlocksView(DyeColor color) {
        return blocksByColor.get(color);
    }

    public Collection<Block> removeAllOfColor(DyeColor color) {
        Collection<Block> removed = getBlocksCopy(color);
        blocksByColor.removeAll(color);
        removed.stream().forEach(block -> colorByBlock.remove(block, color));
        return removed;
    }

    private ArrayList<Block> getBlocksCopy(DyeColor color) {
        return new ArrayList<>(getBlocksView(color));
    }

    public DyeColor removeBlock(Block block) {
        DyeColor color = getColor(block);
        blocksByColor.remove(color, block);
        colorByBlock.remove(block, color);
        return color;
    }

    public Collection<Map.Entry<Block, DyeColor>> getBlockToColorMap() {
        return colorByBlock.entrySet();
    }

    public boolean isEmpty() {
        return blocksByColor.isEmpty();
    }

    public Set<DyeColor> getColorSet() {
        return blocksByColor.keySet();
    }
}
