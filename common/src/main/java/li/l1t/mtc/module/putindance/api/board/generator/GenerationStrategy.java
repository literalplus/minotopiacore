/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.api.board.generator;

import li.l1t.mtc.module.putindance.api.board.Layer;
import org.bukkit.block.Block;

/**
 * Defines the overall strategy to use for generating a board.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface GenerationStrategy {
    void generateAt(Layer layer, Block block);
}
