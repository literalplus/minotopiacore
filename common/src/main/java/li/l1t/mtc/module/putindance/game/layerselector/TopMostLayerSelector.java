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

package li.l1t.mtc.module.putindance.game.layerselector;

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
