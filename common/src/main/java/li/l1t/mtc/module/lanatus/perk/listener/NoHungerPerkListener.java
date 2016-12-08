/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.listener;

import li.l1t.mtc.module.lanatus.perk.api.NoHungerPerk;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Listens for hunger change events on players with a specific metadata key set and cancels them.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class NoHungerPerkListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        HumanEntity entity = event.getEntity();
        if (entity instanceof Player && entity.hasMetadata(NoHungerPerk.TYPE_NAME)) {
            Player player = (Player) entity;
            player.setSaturation(20.0F);
            player.setFoodLevel(20);
            event.setCancelled(true);
        }
    }
}
