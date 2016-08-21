/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
