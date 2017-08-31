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
