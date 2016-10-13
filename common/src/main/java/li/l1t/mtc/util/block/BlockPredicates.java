/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
