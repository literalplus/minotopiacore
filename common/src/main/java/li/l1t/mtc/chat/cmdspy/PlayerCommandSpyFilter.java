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

package li.l1t.mtc.chat.cmdspy;

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
