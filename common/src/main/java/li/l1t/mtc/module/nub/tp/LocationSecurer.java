/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.nub.tp;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Secures a location for teleportation by placing a block below it.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class LocationSecurer {
    public void secureLocation(Location location, Material blockType) {
        Location directlyBelow = location.clone().subtract(0, 1, 0);
        directlyBelow.getBlock().setType(blockType, false);
    }
}
