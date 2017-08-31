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

import li.l1t.mtc.helper.MTCHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class PlayerHideInteractListener implements Listener {
    protected static List<UUID> affectedPlayerIds = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        Player plr = e.getPlayer();
        if (!plr.hasPermission("mtc.hideplayers") ||
                (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) ||
                plr.getItemInHand().getType() != Material.BLAZE_ROD) {
            return;
        }
        e.setCancelled(true);
        if (PlayerHideInteractListener.affectedPlayerIds.contains(plr.getUniqueId())) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                plr.hidePlayer(target);
            }
            PlayerHideInteractListener.affectedPlayerIds.remove(plr.getUniqueId());
            MTCHelper.sendLoc("XU-playershidden", plr, true);
        } else {
            for (Player target : Bukkit.getOnlinePlayers()) {
                plr.showPlayer(target);
            }
            PlayerHideInteractListener.affectedPlayerIds.add(plr.getUniqueId());
            MTCHelper.sendLoc("XU-playersshown", plr, true);
        }
    }
}
