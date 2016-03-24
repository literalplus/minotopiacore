/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission.actions;

import io.github.xxyy.lib.intellij_annotations.NotNull;
import io.github.xxyy.mtc.module.villagertradepermission.VillagerInfo;
import io.github.xxyy.mtc.module.villagertradepermission.VillagerTradePermissionModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

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
    public void execute(@NotNull Player plr, @NotNull Villager selected) {
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
    public void sendActionInfo(@NotNull Player plr) {
        plr.sendMessage("§aDer nächste von dir angeklickte Villager wird für jeden zugänglich sein werden.");
    }

    @Override
    public String getShortDescription() {
        return "Entferne Permission";
    }
}
