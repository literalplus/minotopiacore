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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

/**
 * Prevents enchanting of stacked items. Stacked items only cost once, so enchanting them would be
 * cheaper and therefore muste be prohibited to ensure balance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-01-10
 */
public class EnchantStackPreventModule extends MTCModuleAdapter {
    private EnchantStackPreventListener listener;

    protected EnchantStackPreventModule() {
        super("EnchantStackPrevent", true);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);
        listener = new EnchantStackPreventListener();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable(MTCPlugin plugin) {
        super.disable(plugin);
        HandlerList.unregisterAll(listener);
    }

    private class EnchantStackPreventListener implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onEnchantItem(EnchantItemEvent evt) {
            if (evt.getItem().getAmount() != 1) {
                evt.setCancelled(true);
                evt.getEnchanter().sendMessage("§cDu kannst Items auf MinoTopia nur einzeln enchanten.");
            }
        }
    }
}
