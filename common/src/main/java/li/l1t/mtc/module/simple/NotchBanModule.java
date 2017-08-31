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

package li.l1t.mtc.module.simple;

import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.module.MTCModuleAdapter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Disallows the crafting of any kind of golden apple.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-10
 */
public class NotchBanModule extends MTCModuleAdapter {
    private NotchListener listener;

    protected NotchBanModule() {
        super("NotchBan", false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        listener = new NotchListener();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable(MTCPlugin plugin) {
        super.disable(plugin);
        HandlerList.unregisterAll(listener);
    }

    private class NotchListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onPrepareItemCraft(PrepareItemCraftEvent evt) {
            if (evt.getRecipe().getResult().getType().equals(Material.GOLDEN_APPLE)) {
                evt.getInventory().setItem(0, new ItemStack(Material.AIR));
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onCraftItem(CraftItemEvent evt) {
            if (evt.getRecipe().getResult().getType().equals(Material.GOLDEN_APPLE)) {
                evt.getViewers().forEach(humanEntity ->
                        humanEntity.sendMessage("§cAuf MinoTopia kannst du keine goldenen Äpfel craften! " +
                                "Konsultiere die Händler des Schreckens am Spawn."));
                evt.setCancelled(true);
            }
        }
    }
}
