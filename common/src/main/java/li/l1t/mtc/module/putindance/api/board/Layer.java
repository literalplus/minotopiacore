/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
