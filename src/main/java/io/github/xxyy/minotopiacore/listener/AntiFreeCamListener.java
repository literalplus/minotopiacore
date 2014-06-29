package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;

public class AntiFreeCamListener implements Listener {

    @EventHandler
    public void onInvOpenFreeCam(InventoryOpenEvent e) {
        HumanEntity he = e.getPlayer();
        @SuppressWarnings("deprecation")
        List<Block> lineOfSight = he.getLineOfSight(null, 50); //perfectionists!
        for (int i = 0; i < lineOfSight.size(); i++) {
            if (lineOfSight.get(i).getType() == Material.BEDROCK) {
                Player plr = Bukkit.getPlayerExact(e.getPlayer().getName());
                if (plr == null) {
                    e.setCancelled(true);
                    return;
                }
                plr.sendMessage(MTC.chatPrefix + "Wie kannst du eine Kiste öffnen, die hinter Bedrock ist?!");
                e.setCancelled(true);
                return;
            }
        }
    }
}
