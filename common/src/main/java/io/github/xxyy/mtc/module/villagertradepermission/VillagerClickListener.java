package io.github.xxyy.mtc.module.villagertradepermission;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

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
        VillagerPermissionCommand cmd = module.getVillagerPermissionCommand();
        if (cmd.doAction(plr, villager)) {
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
        }
    }
}
