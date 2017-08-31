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

package li.l1t.mtc.module.villagertradepermission.actions;

import li.l1t.mtc.module.villagertradepermission.VillagerInfo;
import li.l1t.mtc.module.villagertradepermission.VillagerTradePermissionModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import javax.annotation.Nonnull;

/**
 * Shows permission information about the next clicked villager.
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class PermissionInfoAction implements Action {

    private VillagerTradePermissionModule module;

    public PermissionInfoAction(VillagerTradePermissionModule module) {
        this.module = module;
    }

    @Override
    public void execute(@Nonnull Player plr, @Nonnull Villager selected) {
        VillagerInfo villagerInfo = module.findVillagerInfo(selected);
        if (villagerInfo == null) {
            plr.sendMessage("§aFür diesen Villager wurde keine Permission gesetzt.");
            return;
        }
        if (villagerInfo.getPermission() == null) {
            module.removeVillagerInfo(villagerInfo);
            plr.sendMessage("§aFür diesen Villager wurde keine Permission gesetzt.");
            return;
        }
        plr.sendMessage("§aDieser Villager ist mit der Permission §6" + villagerInfo.getPermission() + "§a verfügbar.");
    }

    @Override
    public void sendActionInfo(@Nonnull Player plr) {
        plr.sendMessage("§aDu erhältst Informationen über den nächsten angeklickten Villager.");
    }

    @Override
    public String getShortDescription() {
        return "Zeige Permission";
    }
}
