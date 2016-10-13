/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game.layerselector;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.NoSuchLayerException;
import li.l1t.mtc.module.putindance.api.game.LayerSelector;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

/**
 * Randomly selects any layer from a board, however preferring the topmost layer.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public class AnyLayerSelector implements LayerSelector {
    @Override
    public Layer selectLayer(Board board) throws NoSuchLayerException {
        if (!board.hasActiveLayers()) {
            throw new NoSuchLayerException("no more active layers");
        }
        if (RandomUtils.nextInt(10) == 0) {
            return board.getTopMostActiveLayer();
        }
        List<Layer> activeLayers = board.getActiveLayers();
        return activeLayers.get(RandomUtils.nextInt(activeLayers.size()));
    }
}
