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

package li.l1t.mtc.module.lanatus.perk;

import li.l1t.mtc.yaml.ManagedConfiguration;

/**
 * Provides some common configurable properties and other properties about the Perks module.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class PerksConfig {

    private static final String MAX_CONCURRENT_PERKS_PATH = "concurrent-perk-limit";
    private int concurrentPerkLimit = 3;
    private boolean shopModuleAvailable = false;

    public void reloadFrom(LanatusPerkModule module) {
        ManagedConfiguration configuration = module.getConfig();
        configuration.options().copyDefaults(true);
        configuration.addDefault(MAX_CONCURRENT_PERKS_PATH, concurrentPerkLimit);
        concurrentPerkLimit = configuration.getInt(MAX_CONCURRENT_PERKS_PATH);
        shopModuleAvailable = module.isLanatusShopAvailable();
        module.save();
    }

    public int getConcurrentPerkLimit() {
        return concurrentPerkLimit;
    }

    public boolean isLanatusShopAvailable() {
        return shopModuleAvailable;
    }
}
