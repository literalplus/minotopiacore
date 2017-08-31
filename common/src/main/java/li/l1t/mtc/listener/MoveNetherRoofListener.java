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

package li.l1t.mtc.listener;

import li.l1t.mtc.MTC;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public final class MoveNetherRoofListener implements Listener {
    private final MTC plugin;

    public MoveNetherRoofListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNetherMove(PlayerMoveEvent e) {
        if (!e.getTo().getWorld().getEnvironment().equals(Environment.NETHER)) {
            return;
        }
        if (e.getTo().getBlockY() > 125) {
            if (e.getPlayer().hasPermission("mtc.ignore")) {
                return;
            }
            e.getPlayer().sendMessage(MTC.chatPrefix + "§eDu darfst nicht über das Netherdach :)");
            e.getPlayer().sendMessage(MTC.chatPrefix + "§eViel Spaß beim Spawn.");
            e.getPlayer().sendMessage(MTC.chatPrefix + "§eDeine Koordinaten: §a" + e.getPlayer().getLocation().toString());
            e.getPlayer().sendMessage(MTC.chatPrefix + "§eWenn du mit dieser automatisierten Entscheidung unzufrieden bist, wende dich " +
                    "bitte mit Screenshot dieser Nachricht an das Team! (Forum!)");

            if (plugin.getXLoginHook().getSpawnLocation() == null) {
                e.setTo(e.getFrom());
            } else {
                e.getPlayer().teleport(plugin.getXLoginHook().getSpawnLocation());
            }
        }
    }
}
