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

package li.l1t.mtc.module.villagertradepermission;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import javax.annotation.Nonnull;

/**
 * Checks for right-clicks on villagers.
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class VillagerClickListener implements Listener {
    private final VillagerTradePermissionModule module;

    public VillagerClickListener(@Nonnull VillagerTradePermissionModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) {
            return;
        }
        Villager villager = (Villager) event.getRightClicked();

        Player plr = event.getPlayer();

        //check whether an action was scheduled by a command of that player before
        if (module.getActionManager().doAction(plr, villager)) {
            event.setCancelled(true);
            plr.closeInventory();
            return;
        }

        if (plr.hasPermission("mtc.ignore")) { //have a super permission to ignore mtc checks
            return;
        }

        //Check permission
        VillagerInfo villagerInfo = module.findVillagerInfo(villager);
        if (villagerInfo == null) {
            return;
        }
        String permission = villagerInfo.getPermission();
        if (permission != null && !plr.hasPermission(permission)) {
            event.setCancelled(true);
            plr.closeInventory();
            plr.sendMessage("§cDu kannst diesen Villager nicht benutzen.");
        }
    }
}
