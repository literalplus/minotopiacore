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
