/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.yaml.ManagedConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Manages the configuration file of PutinDance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
class PutinDanceConfig {
    private static final String FIRST_BOUNDARY_PATH = "boundaries.first";
    private static final String SECOND_BOUNDARY_PATH = "boundaries.second";
    private static final String SPAWN_LOCATION_PATH = "spawn";
    private XyLocation firstBoardBoundary;
    private XyLocation secondBoardBoundary;
    private XyLocation spawnLocation;

    public PutinDanceConfig() {
        ConfigurationSerialization.registerClass(XyLocation.class);
    }

    public void loadFrom(ManagedConfiguration config) {
        firstBoardBoundary = (XyLocation) config.get(FIRST_BOUNDARY_PATH);
        secondBoardBoundary = (XyLocation) config.get(SECOND_BOUNDARY_PATH);
        spawnLocation = (XyLocation) config.get(SPAWN_LOCATION_PATH);
    }

    public void saveTo(ManagedConfiguration config) {
        config.set(FIRST_BOUNDARY_PATH, firstBoardBoundary);
        config.set(SECOND_BOUNDARY_PATH, secondBoardBoundary);
        config.set(SPAWN_LOCATION_PATH, spawnLocation);
    }

    public XyLocation getFirstBoardBoundary() {
        return firstBoardBoundary;
    }

    public void setFirstBoardBoundary(XyLocation firstBoardBoundary) {
        this.firstBoardBoundary = firstBoardBoundary;
    }

    public XyLocation getSecondBoardBoundary() {
        return secondBoardBoundary;
    }

    public void setSecondBoardBoundary(XyLocation secondBoardBoundary) {
        this.secondBoardBoundary = secondBoardBoundary;
    }

    public XyLocation getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(XyLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
