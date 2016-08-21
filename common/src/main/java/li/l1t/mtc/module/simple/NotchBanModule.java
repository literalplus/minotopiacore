/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
