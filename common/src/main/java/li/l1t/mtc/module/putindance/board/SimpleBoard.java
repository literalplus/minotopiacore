/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.board;

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.Layer;
import li.l1t.mtc.module.putindance.api.board.NoSuchLayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple implementation of a PutinDance board.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class SimpleBoard implements Board {
    private final List<Layer> allLayers = new ArrayList<>();
    private final XyLocation firstBoundary;
    private final XyLocation secondBoundary;
    private final int minYLevel;
    private final int maxYLevel;

    public SimpleBoard(XyLocation firstBoundary, XyLocation secondBoundary) {
        this.firstBoundary = firstBoundary;
        this.secondBoundary = secondBoundary;
        maxYLevel = Math.max(firstBoundary.getBlockY(), secondBoundary.getBlockY());
        minYLevel = Math.min(firstBoundary.getBlockY(), secondBoundary.getBlockY());
    }

    @Override
    public int getMinYLevel() {
        return minYLevel;
    }

    @Override
    public int getMaxYLevel() {
        return maxYLevel;
    }

    @Override
    public void addLayer(Layer layer) {
        Preconditions.checkNotNull(layer, "layer");
        Preconditions.checkArgument(!allLayers.contains(layer), "layer already in board");
        allLayers.add(layer);
    }

    @Override
    public List<Layer> getAllLayers() {
        return allLayers;
    }

    @Override
    public int getLayerCount() {
        return getAllLayers().size();
    }

    @Override
    public List<Layer> getActiveLayers() {
        return allLayers.stream()
                .filter(Layer::hasBlocksLeft)
                .collect(Collectors.toList());
    }

    @Override
    public int getActiveLayerCount() {
        return getActiveLayers().size();
    }

    @Override
    public boolean hasActiveLayers() {
        return getActiveLayerCount() > 0;
    }

    @Override
    public Layer getTopMostActiveLayer() throws NoSuchLayerException {
        return getActiveLayersOrFailIfEmpty().get(0);
    }

    private List<Layer> getActiveLayersOrFailIfEmpty() {
        List<Layer> activeLayers = getActiveLayers();
        if (activeLayers.isEmpty()) {
            throw new NoSuchLayerException("no more active layers!");
        }
        return activeLayers;
    }

    @Override
    public Layer getNextActiveLayerBelow(Layer referenceLayer) throws NoSuchLayerException {
        List<Layer> activeLayers = getActiveLayersOrFailIfEmpty();
        int referenceIndex = activeLayers.indexOf(referenceLayer);
        if (referenceIndex == -1) {
            throw new NoSuchLayerException("failed to get next layer below: reference layer is inactive");
        }
        int belowIndex = referenceIndex + 1;
        if (belowIndex >= activeLayers.size()) {
            throw new NoSuchLayerException("no active layer below reference layer");
        }
        return activeLayers.get(belowIndex);
    }

    @Override
    public XyLocation getFirstBoundary() {
        return firstBoundary;
    }

    @Override
    public XyLocation getSecondBoundary() {
        return secondBoundary;
    }
}
