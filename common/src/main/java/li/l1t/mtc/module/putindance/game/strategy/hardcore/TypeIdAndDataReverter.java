/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game.strategy.hardcore;

import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.util.block.AbstractTransformTask;
import li.l1t.mtc.util.block.RevertableBlockTransformer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Reverts block states by setting type id and data instead of updating the state.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
public class TypeIdAndDataReverter extends AbstractTransformTask {
    private final RevertableBlockTransformer blockTransformer;
    private final Layer layer;
    private Queue<Map.Entry<Block, DyeColor>> revertQueue = null;

    public TypeIdAndDataReverter(RevertableBlockTransformer blockTransformer, Layer layer) {
        this.blockTransformer = blockTransformer;
        this.layer = layer;
    }

    @Override
    public void run() {
        for (int i = 0; i < blockTransformer.getBlocksPerTick(); i++) {
            if (queue().isEmpty()) {
                setDoneAndCancel();
                return;
            }
            Map.Entry<Block, DyeColor> toRevert = queue().poll();
            revertTypeIdAndData(toRevert);
        }
    }

    @SuppressWarnings("deprecation")
    private void revertTypeIdAndData(Map.Entry<Block, DyeColor> toRevert) {
        byte data = toRevert.getValue().getWoolData();
        Block block = toRevert.getKey();
        block.setTypeIdAndData(Material.WOOL.getId(), data, false);
//        toRevert.getBlock().setTypeIdAndData(toRevert.getTypeId(), toRevert.getRawData(), false);
    }

    private Queue<Map.Entry<Block, DyeColor>> queue() {
        if (revertQueue == null) {
            revertQueue = new LinkedList<>(layer.getBlocks());
        }
        return revertQueue;
    }
}
