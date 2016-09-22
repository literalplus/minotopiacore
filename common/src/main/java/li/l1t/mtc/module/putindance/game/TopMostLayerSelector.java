/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.game.LayerSelector;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Selects the top layer. If the top layer has a low amount of colours left, there is a chance the
 * next active layer will be selected instead.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public class TopMostLayerSelector implements LayerSelector {
    @Override
    public Layer selectLayer(Board board) {
        Layer layer = board.getTopMostActiveLayer();
        if (isEligibleForLowerLayerSelection(board, layer)) {
            layer = board.getNextActiveLayerBelow(layer);
        }
        return layer;
    }

    private boolean isEligibleForLowerLayerSelection(Board board, Layer layer) {
        return layer.getActiveColors().size() <= 2 &&
                board.getActiveLayerCount() > 1 &&
                RandomUtils.nextInt(4) == 0;
    }
}
