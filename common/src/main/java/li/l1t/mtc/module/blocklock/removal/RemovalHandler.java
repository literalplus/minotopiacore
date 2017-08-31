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

package li.l1t.mtc.module.blocklock.removal;

import li.l1t.common.exception.NonSensitiveException;
import li.l1t.mtc.module.blocklock.api.BlockLock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the removal of a block lock.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-02-08
 */
public interface RemovalHandler {
    /**
     * Handles the removal of a block lock by a player.
     *
     * @param lock   the lock to remove
     * @param player the player who initiated the removal
     * @return whether given player should get the block refunded
     * @throws NonSensitiveException if the removal should not occur
     */
    boolean onRemove(BlockLock lock, Player player);

    /**
     * Describes the effect of this handler to a command sender, so that they may consider whether they actually want to
     * destroy the block.
     *
     * @param sender the sender to describe this handler to
     */
    void describeTo(CommandSender sender);
}
