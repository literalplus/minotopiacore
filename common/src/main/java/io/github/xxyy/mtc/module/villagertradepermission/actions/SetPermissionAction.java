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
 * Sets the permission on the next clicked villager
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public final class SetPermissionAction implements Action {

    private VillagerTradePermissionModule module;
    @NotNull
    private final String newPermission;

    public SetPermissionAction(VillagerTradePermissionModule module, @NotNull String newPermission) {
        this.module = module;
        this.newPermission = newPermission;
    }

    @Override
    public void execute(@NotNull Player plr, @NotNull Villager selected) {
        VillagerInfo villagerInfo = module.findVillagerInfo(selected);
        if (villagerInfo == null) {
            villagerInfo = VillagerInfo.forEntity(selected);
            module.addVillagerInfo(villagerInfo);
        }
        String oldPermission = villagerInfo.getPermission();
        villagerInfo.setPermission(newPermission);
        module.save();
        plr.sendMessage("§aDieser Villager ist nun mit der Permission §6" + newPermission + " §averfügbar.");
        if (oldPermission == null) {
            plr.sendMessage("§aVorher war er jedem zugänglich.");
        } else {
            plr.sendMessage("§aVorher war er mit der Permission §6" + oldPermission + " §azugänglich.");
        }
    }

    @Override
    public void sendActionInfo(@NotNull Player plr) {
        plr.sendMessage("§aDer nächste von dir angeklickte Villager wird mit der Permission §6" + newPermission + " §averfügbar sein.");
    }

    @Override
    public String getShortDescription() {
        return "Setze Permission zu " + newPermission;
    }
}
