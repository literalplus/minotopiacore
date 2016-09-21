/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util.block;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.function.Consumer;

/**
 * Allows to replace huge amounts of blocks with the capability to divide the workload into multiple
 * executions.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface BlockTransformer {
    World getWorld();

    int getCurrentXPosition();

    int getCurrentYPosition();

    int getCurrentZPosition();

    void setTransformerFunction(Consumer<Block> transformer);

    TransformTask createTransformTask();

    void setBlocksPerTick(int newBlocksPerTick);

    int getBlocksPerTick();
}
