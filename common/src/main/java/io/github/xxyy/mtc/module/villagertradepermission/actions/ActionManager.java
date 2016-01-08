/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission.actions;

import com.google.common.collect.Maps;
import io.github.xxyy.mtc.module.villagertradepermission.VillagerTradePermissionModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Manages actions scheduled for players
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class ActionManager {
    private final Map<UUID, Action> scheduledActions = Maps.newHashMapWithExpectedSize(2);
    private final LeaveListener listener;

    public ActionManager(VillagerTradePermissionModule module) {
        listener = new LeaveListener();
        module.getPlugin().getServer().getPluginManager().registerEvents(listener, module.getPlugin()); //anti memory leak //click listener not here as it interferes with permission check
    }

    public Action getScheduledAction(Player plr) {
        return scheduledActions.get(plr.getUniqueId());
    }

    /**
     * Removes the scheduled action for the given player
     *
     * @param plr the player whose current scheduled action should be cancelled
     * @return the previously scheduled action
     * @see Map#remove(Object)
     */
    public Action removeScheduledAction(Player plr) {
        return scheduledActions.remove(plr.getUniqueId());
    }

    public void clearCache() {
        scheduledActions.clear();
    }

    /**
     * Stop the internal leave listener and clears the cache
     */
    public void disable() {
        HandlerList.unregisterAll(listener);
        clearCache();
    }

    public <T extends Action> T scheduleAction(Player plr, T action) {
        scheduledActions.put(plr.getUniqueId(), action);
        return action;
    }

    public boolean hasAction(Player plr) {
        return scheduledActions.containsKey(plr.getUniqueId());
    }

    /**
     * Checks whether an action was scheduled per command on next villager click and executes it.
     *
     * @param plr      the player who clicked the villager
     * @param selected the clicked villager
     * @return whether an action was scheduled (if yes, normal click behaviour should be cancelled)
     */
    public boolean doAction(@NotNull Player plr, @NotNull Villager selected) {
        if (!plr.hasPermission(VillagerTradePermissionModule.COMMAND_PERMISSION)) {
            return false;
        }
        Action action = scheduledActions.get(plr.getUniqueId());
        if (action == null) {
            return false;
        }
        action.execute(plr, selected);
        scheduledActions.remove(plr.getUniqueId());
        return true;
    }

    private final class LeaveListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerLeave(PlayerQuitEvent event) {
            scheduledActions.remove(event.getPlayer().getUniqueId());
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerLeave(PlayerKickEvent event) {
            scheduledActions.remove(event.getPlayer().getUniqueId());
        }
    }
}
