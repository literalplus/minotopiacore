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

package li.l1t.mtc.module.putindance.api.board;

import li.l1t.common.misc.XyLocation;

import java.util.List;

/**
 * Represents the physical board used for playing a game of PutinDance, storing its state and
 * layers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public interface Board {
    XyLocation getFirstBoundary();

    XyLocation getSecondBoundary();

    int getMaxYLevel();

    int getMinYLevel();

    List<Layer> getAllLayers();

    List<Layer> getActiveLayers();

    boolean hasActiveLayers();

    void addLayer(Layer layer);

    Layer getTopMostActiveLayer() throws NoSuchLayerException;

    /**
     * Gets the next active layer below an active reference layer
     *
     * @param referenceLayer the reference layer
     * @return the next active layer below given layer
     * @throws NoSuchLayerException if the reference layer is not an active layer, or there is no
     *                              active layer below it
     */
    Layer getNextActiveLayerBelow(Layer referenceLayer) throws NoSuchLayerException;

    int getLayerCount();

    int getActiveLayerCount();
}
