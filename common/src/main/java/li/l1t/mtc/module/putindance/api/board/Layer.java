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

package li.l1t.mtc.module.putindance.api.board;

import li.l1t.common.misc.XyLocation;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents a single layer of blocks in a PutinDance board, storing the blocks the layer consists
 * of by colour.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public interface Layer {
    Board getBoard();

    XyLocation getFirstBoundary();

    XyLocation getSecondBoundary();

    int getYLevel();

    boolean hasBlocksLeft();

    Collection<Block> getBlocksByColor(DyeColor color);

    Collection<Map.Entry<Block, DyeColor>> getBlocks();

    Set<DyeColor> getActiveColors();

    void addBlock(Block block);

    /**
     * Removes a block from this layer.
     *
     * @param block the block to remove
     */
    void removeBlock(Block block);

    /**
     * Removes all blocks of a color from this layer.
     *
     * @param color the color of blocks to remove
     * @return the collection of removed blocks
     */
    Collection<Block> removeBlocksByColor(DyeColor color);
}
