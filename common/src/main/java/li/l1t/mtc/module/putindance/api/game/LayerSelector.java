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
