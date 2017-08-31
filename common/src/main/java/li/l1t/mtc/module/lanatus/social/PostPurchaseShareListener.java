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
