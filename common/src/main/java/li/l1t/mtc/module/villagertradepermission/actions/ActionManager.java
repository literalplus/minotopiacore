/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.module.villagertradepermission.actions;

import com.google.common.collect.Maps;
import li.l1t.mtc.module.villagertradepermission.VillagerTradePermissionModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

/**
 * Manages actions scheduled for players
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public class ActionManager {
    private final Map<UUID, Action> scheduledActions = Maps.newHashMapWithExpectedSize(2);
    private final LeaveListener listener = new LeaveListener();
    private final VillagerTradePermissionModule module;

    public ActionManager(VillagerTradePermissionModule module) {
        this.module = module;
    }

    public void onEnable() {
        module.getPlugin().getServer().getPluginManager().registerEvents(listener, module.getPlugin()); //click listener not here as it interferes with permission check
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
    public boolean doAction(@Nonnull Player plr, @Nonnull Villager selected) {
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
