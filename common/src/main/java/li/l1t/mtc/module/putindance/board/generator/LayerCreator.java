/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board.generator;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.board.MapLayer;

/**
 * Creates layer instances for all the layers defined by a board's boundaries.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
class LayerCreator {
    public void addLayersTo(Board board) {
        for (int y = getFirstLayerY(board); y >= getLastLayerY(board); y--) {
            board.addLayer(new MapLayer(y, board));
        }
    }

    private int getLayerCount(Board board) {
        return Math.abs(board.getFirstBoundary().getBlockY() - board.getSecondBoundary().getBlockY());
    }

    private int getFirstLayerY(Board board) {
        return Math.max(board.getFirstBoundary().getBlockY(), board.getSecondBoundary().getBlockY());
    }

    private int getLastLayerY(Board board) {
        return Math.min(board.getFirstBoundary().getBlockY(), board.getSecondBoundary().getBlockY());
    }
}
