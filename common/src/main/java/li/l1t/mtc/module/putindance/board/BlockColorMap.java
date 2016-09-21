/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public boolean isEmpty() {
        return blocksByColor.isEmpty();
    }

    public Set<DyeColor> getColorSet() {
        return blocksByColor.keySet();
    }
}
