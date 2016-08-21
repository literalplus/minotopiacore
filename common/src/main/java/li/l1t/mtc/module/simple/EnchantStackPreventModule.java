/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
