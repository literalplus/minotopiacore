/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission;

import io.github.xxyy.lib.intellij_annotations.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Checks for right-clicks on villagers.
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class VillagerClickListener implements Listener {
    private final VillagerTradePermissionModule module;

    public VillagerClickListener(@NotNull VillagerTradePermissionModule module) {
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
