/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.social;

import li.l1t.common.chat.XyComponentBuilder;
import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.mtc.api.chat.ChatConstants;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listens for post purchase events and offers players to share what they purchased in the global chat.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-13
 */
public class PostPurchaseShareListener implements Listener {
    private final LanatusSocialModule module;

    public PostPurchaseShareListener(LanatusSocialModule module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostPurchase(PostPurchaseEvent event) {
        Player player = event.getPlayer();
        module.markShareable(event.getPurchase());
        player.spigot().sendMessage(
                new XyComponentBuilder(ChatConstants.LEFT_ARROW, ChatColor.GOLD)
                        .append(" Danke für deinen Einkauf! Möchtest du das im globalen Chat teilen, damit alle anderen Bescheid wissen? ")
                        .append("[Teilen] ", ChatColor.DARK_PURPLE).hintedCommand("/lashare share " + event.getPurchase().getUniqueId())
                        .append("[Vorschau]", ChatColor.DARK_GREEN).hintedCommand("/lashare preview " + event.getPurchase().getUniqueId())
                        .create()
        );
    }
}
