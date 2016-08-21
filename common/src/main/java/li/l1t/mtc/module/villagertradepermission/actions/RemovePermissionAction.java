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
 * Removes the permission from the next clicked villager
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class RemovePermissionAction implements Action {

    private VillagerTradePermissionModule module;

    public RemovePermissionAction(VillagerTradePermissionModule module) {
        this.module = module;
    }

    @Override
    public void execute(@Nonnull Player plr, @Nonnull Villager selected) {
        VillagerInfo villagerInfo = module.findVillagerInfo(selected);
        if (villagerInfo == null) {
            plr.sendMessage("§cDieser Villager ist bereits jedem zugänglich.");
            return;
        }
        String oldPermission = villagerInfo.getPermission();
        if (oldPermission == null) {
            module.removeVillagerInfo(villagerInfo);
            plr.sendMessage("§cDieser Villager ist bereits jedem zugänglich.");
            return;
        }
        villagerInfo.setPermission(null);
        module.save();
        plr.sendMessage("§aDieser Villager ist nun jedem zugänglich.");
        plr.sendMessage("§aVorher war er mit der Permission §6" + oldPermission + " §azugänglich.");
    }

    @Override
    public void sendActionInfo(@Nonnull Player plr) {
        plr.sendMessage("§aDer nächste von dir angeklickte Villager wird für jeden zugänglich sein werden.");
    }

    @Override
    public String getShortDescription() {
        return "Entferne Permission";
    }
}
