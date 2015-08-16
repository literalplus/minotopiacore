/*
 * Copyright (c) 2013-2015.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import io.github.xxyy.common.localisation.LangHelper;
import io.github.xxyy.mtc.MTC;

import java.util.List;
import java.util.Set;

public final class EnderPearlProjectileLaunchListener implements Listener {

    private final MTC plugin;

    public EnderPearlProjectileLaunchListener(MTC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunchEP(ProjectileLaunchEvent e) {
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) {
            return;
        }
        ProjectileSource projectileSource = e.getEntity().getShooter();
        if (!(projectileSource instanceof Player)) {
            return;
        }

        Player shooter = (Player) projectileSource;

        if (!shooter.hasPermission("mtc.enderpearl.use")) {
            e.setCancelled(true);
            shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epcancelled", shooter.getName(), plugin.getName()));
            EnderPearlProjectileLaunchListener.returnPearl(shooter);
            return;
        }
        List<Block> lineOfSight = shooter.getLineOfSight((Set<Material>) null, 100);
        for (Block lineOfSightItem : lineOfSight) {
            if (lineOfSightItem.getType() == Material.BEDROCK) {
                e.setCancelled(true);
                shooter.sendMessage(MTC.chatPrefix + LangHelper.localiseString("XU-epbedrock", shooter.getName(), plugin.getName()));
                EnderPearlProjectileLaunchListener.returnPearl(shooter);
                return;
            }
        }
    }

    private static void returnPearl(Player plr) {
        if (plr.getItemInHand().getType() != Material.ENDER_PEARL) {
            return;
        }
        ItemStack is = plr.getItemInHand();
        is.setAmount(plr.getItemInHand().getAmount() + 1);
        plr.setItemInHand(is);
    }
}
