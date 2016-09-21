/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board.api;

import java.util.List;

/**
 * Represents the physical board used for playing a game of PutinDance, storing its state and
 * layers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public interface Board {
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
