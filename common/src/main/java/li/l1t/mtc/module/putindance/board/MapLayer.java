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

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a layer using a map for block storage.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class MapLayer implements Layer {
    private final int yLevel;
    private final Board board;
    private final XyLocation firstBoundary;
    private final XyLocation secondBoundary;
    private BlockColorMap blockColorMap = new BlockColorMap();

    public MapLayer(int yLevel, Board board) {
        this.yLevel = yLevel;
        this.board = board;
        this.firstBoundary = adjustToLayerY(board.getFirstBoundary());
        this.secondBoundary = adjustToLayerY(board.getSecondBoundary());
    }

    private XyLocation adjustToLayerY(XyLocation initial) {
        XyLocation loc = new XyLocation(initial);
        loc.setY(yLevel);
        return loc;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public int getYLevel() {
        return yLevel;
    }

    @Override
    public XyLocation getFirstBoundary() {
        return firstBoundary;
    }

    @Override
    public XyLocation getSecondBoundary() {
        return secondBoundary;
    }

    @Override
    public boolean hasBlocksLeft() {
        return !blockColorMap.isEmpty();
    }

    @Override
    public Collection<Block> getBlocksByColor(DyeColor color) {
        Preconditions.checkNotNull(color, "color");
        return blockColorMap.getBlocksView(color);
    }

    @Override
    public Collection<Map.Entry<Block, DyeColor>> getBlocks() {
        return blockColorMap.getBlockToColorMap();
    }

    @Override
    public Set<DyeColor> getActiveColors() {
        return blockColorMap.getColorSet();
    }

    @Override
    public void addBlock(Block block) {
        Preconditions.checkNotNull(block, "block");
        DyeColor color = findDyeColorOrFail(block);
        blockColorMap.put(color, block);
    }

    @SuppressWarnings("deprecation")
    private DyeColor findDyeColorOrFail(Block block) {
        Preconditions.checkArgument(block.getType() == Material.WOOL, "invalid block type %s; valid: wool", block.getType());
        byte rawData = block.getData();
        DyeColor dyeColor = DyeColor.getByWoolData(rawData);
        Preconditions.checkArgument(dyeColor != null, "invalid color data on %s: %d", block, rawData);
        return dyeColor;
    }

    @Override
    public void removeBlock(Block block) {
        blockColorMap.removeBlock(block);
    }

    @Override
    public Collection<Block> removeBlocksByColor(DyeColor color) {
        return blockColorMap.removeAllOfColor(color);
    }
}
