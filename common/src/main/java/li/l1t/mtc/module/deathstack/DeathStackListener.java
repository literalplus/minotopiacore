/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.deathstack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Listens for item drop events and automatically stacks the items.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-09
 */
public class DeathStackListener implements Listener {
    private final DeathStackModule module;

    public DeathStackListener(DeathStackModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDeathEvent event) {
        List<ItemStack> compactedDrops = module.compactStacks(event.getDrops());
        event.getDrops().clear();
        event.getDrops().addAll(compactedDrops);
    }
}
