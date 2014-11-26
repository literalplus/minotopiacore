/*
 * Copyright (c) 2013-2014.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.listener;

import io.github.xxyy.mtc.ConfigHelper;
import io.github.xxyy.mtc.helper.StatsHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public final class StatsDeathListener implements Listener {
    @EventHandler(priority=EventPriority.LOW)
    public void onDeath(PlayerDeathEvent e){
        Player plr = e.getEntity();
        Player killer = plr.getKiller();
        if(ConfigHelper.isStatsEnabled()){
            if(killer != null){
                StatsHelper.cacheModification(killer.getName(), true);
            }
            StatsHelper.cacheModification(plr.getName(), false);
        }
    }
}
