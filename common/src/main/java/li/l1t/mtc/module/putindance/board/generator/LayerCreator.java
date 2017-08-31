/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
