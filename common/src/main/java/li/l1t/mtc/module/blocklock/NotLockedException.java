/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.blocklock;

import li.l1t.common.exception.UserException;
import li.l1t.common.util.LocationHelper;
import org.bukkit.block.Block;

/**
 * Thrown if a block does not have an associated lock, but a lock was required for an action.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-13
 */
public class NotLockedException extends UserException {
    private final Block block;

    public NotLockedException(Block block) {
        super("Dieser Block ist nicht geschützt: %s bei %s", block.getType(), LocationHelper.prettyPrint(block.getLocation()));
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
