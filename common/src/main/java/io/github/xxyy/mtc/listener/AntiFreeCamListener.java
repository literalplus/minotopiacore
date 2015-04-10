/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import io.github.xxyy.mtc.MTC;

import java.util.List;
import java.util.Set;

public final class AntiFreeCamListener implements Listener {

    @EventHandler
    public void onInvOpenFreeCam(InventoryOpenEvent e) {
        HumanEntity he = e.getPlayer();
        List<Block> lineOfSight = he.getLineOfSight((Set<Material>) null, 50); //perfectionists!
        for (Block aLineOfSight : lineOfSight) {
            if (aLineOfSight.getType() == Material.BEDROCK) {
                Player plr = (Player) e.getPlayer(); //Bukkit.getPlayerExact(e.getPlayer().getName()); TODO Find out wtf this was for
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
