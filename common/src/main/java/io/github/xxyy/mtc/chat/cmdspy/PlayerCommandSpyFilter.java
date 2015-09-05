/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.chat.cmdspy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public class PlayerCommandSpyFilter extends MultiSubscriberCommandSpyFilter {
    private final UUID targetId;

    public PlayerCommandSpyFilter(String notificationFormat, UUID target) {
        super(notificationFormat, (cmd, plr) -> target.equals(plr.getUniqueId()));
        this.targetId = target;
    }

    public UUID getTargetId() {
        return targetId;
    }

    @Override
    public boolean matches(String command, Player sender) {
        getPlayer(); //TODO: note that that's executed at every command call ._. - better try a WeakReference

        return super.matches(command, sender);
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(targetId);
        if (player == null) {
            CommandSpyFilters.unregisterFilter(this);
        }
        return player;
    }

    public String getPlayerName() {
        Player plr = getPlayer();
        return plr == null ? "{offline: " + targetId + "}" : plr.getName();
    }

    @Override
    public String niceRepresentation() {
        return super.niceRepresentation() + " -> " + targetId.toString() + "@" + getPlayerName();
    }
}
