/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.tp;

import li.l1t.mtc.api.module.inject.InjectMe;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Selects random coordinates in the range defined in the configuration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class CoordinateSelector {
    private final NubTpConfig config;

    @InjectMe
    public CoordinateSelector(NubTpConfig config) {
        this.config = config;
    }

    public Location selectLocation(World world) {
        int x = selectHorizontalCoordinate();
        int z = selectHorizontalCoordinate();
        int y = world.getHighestBlockYAt(x, z) + 4;
        return new Location(world, x, y, z);
    }

    private int selectHorizontalCoordinate() {
        int baseValue = RandomUtils.nextInt(config.getMinCoordinate(), config.getMaxCoordinate());
        if(RandomUtils.nextInt(0, 2) == 0) {
            return baseValue * -1;
        } else {
            return baseValue;
        }
    }
}
