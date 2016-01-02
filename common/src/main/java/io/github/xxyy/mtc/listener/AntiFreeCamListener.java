/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import io.github.xxyy.mtc.MTC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.List;
import java.util.Set;

public final class AntiFreeCamListener implements Listener {

    @EventHandler
    public void onInvOpenFreeCam(InventoryOpenEvent e) {
        HumanEntity he = e.getPlayer();
        if (!(he instanceof Player) || he.hasPermission("mtc.ignore")) {
            return;
        }
        List<Block> lineOfSight = he.getLineOfSight((Set<Material>) null, 8); //perfectionists!
        for (Block aLineOfSight : lineOfSight) {
            if (aLineOfSight.getType() == Material.BEDROCK) {
                he.sendMessage(MTC.chatPrefix + "Wie kannst du eine Kiste öffnen, die hinter Bedrock ist?!");
                e.setCancelled(true);
                return;
            }
        }
    }
}
