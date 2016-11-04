/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
