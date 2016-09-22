/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.api.game;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.NoSuchLayerException;

/**
 * Selects the layer a tick strategy operates on.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-22
 */
public interface LayerSelector {
    /**
     * Selects the layer to operate on for a given board.
     *
     * @param board the board
     * @return the selected layer
     * @throws NoSuchLayerException if there is no eligible layer for selection
     */
    Layer selectLayer(Board board) throws NoSuchLayerException;
}
