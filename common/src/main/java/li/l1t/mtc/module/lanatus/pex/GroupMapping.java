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

package li.l1t.mtc.module.lanatus.pex;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Checks whether a given Lanatus account's group can be applied to the local PermissionsEx
 * configuration.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-30
 */
public class GroupMapping {
    private final Map<String, String> lanatusToPexGroupMap = new HashMap<>();

    public void clear() {
        lanatusToPexGroupMap.clear();
    }

    public void mapLanatusToPexGroup(String lanatusGroupName, String pexGroupName) {
        lanatusToPexGroupMap.put(lanatusGroupName, pexGroupName);
    }

    public Optional<String> findPexGroupFor(String lanatusGroupName) {
        return Optional.ofNullable(lanatusToPexGroupMap.get(lanatusGroupName));
    }
}
