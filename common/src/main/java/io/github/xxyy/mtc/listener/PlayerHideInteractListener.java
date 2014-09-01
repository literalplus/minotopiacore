package io.github.xxyy.mtc.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.xxyy.mtc.helper.MTCHelper;

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
