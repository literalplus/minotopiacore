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
