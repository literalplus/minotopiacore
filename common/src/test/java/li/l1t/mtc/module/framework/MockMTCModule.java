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

package li.l1t.mtc.module.framework;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.module.MTCModuleAdapter;

class MockMTCModule extends MTCModuleAdapter {
    protected MockMTCModule(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("RefusedBequest")
    public boolean canBeEnabled(MTCPlugin plugin) {
        return true;
    }
}
