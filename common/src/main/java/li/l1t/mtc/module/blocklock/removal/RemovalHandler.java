/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
