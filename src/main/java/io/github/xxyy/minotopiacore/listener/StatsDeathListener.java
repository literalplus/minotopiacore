package io.github.xxyy.minotopiacore.listener;

import io.github.xxyy.minotopiacore.ConfigHelper;
import io.github.xxyy.minotopiacore.helper.StatsHelper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public class StatsDeathListener implements Listener {
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
