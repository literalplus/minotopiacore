package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.MTC;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class AntiFreeCamListener implements Listener {

    @EventHandler
    public void onInvOpenFreeCam(InventoryOpenEvent e) {
        HumanEntity he = e.getPlayer();
        @SuppressWarnings("deprecation")
        List<Block> LoS = he.getLineOfSight(null, 50); //perfectionists!
        for (short i = 0; i < LoS.size(); i++) {
            if (LoS.get(i).getType() == Material.BEDROCK) {
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
