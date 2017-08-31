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

package li.l1t.mtc.module.lanatus.base.scoreboard;

import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import li.l1t.mtc.module.scoreboard.CommonScoreboardProvider;
import li.l1t.mtc.module.scoreboard.LambdaBoardItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles displaying Lanatus-specific data and provides an event listener that attempts to keep the scoreboard in sync
 * with melons count as much as possible.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-15
 */
public class LanatusScoreboardHandler implements Listener {
    private final LanatusClient client;
    private final CommonScoreboardProvider scoreboard;
    private final LambdaBoardItem melonItem = new LambdaBoardItem("la-melons", "§6Melonen:", this::showMelonCount);

    @InjectMe
    public LanatusScoreboardHandler(MTCLanatusClient client, CommonScoreboardProvider scoreboard) {
        this.client = client;
        this.scoreboard = scoreboard;
    }

    public void enable() {
        scoreboard.registerBoardItem(melonItem);
    }

    private int showMelonCount(Player player) {
        return client.accounts().findOrDefault(player.getUniqueId()).getMelonsCount();
    }

    @EventHandler
    public void onPostPurchase(PostPurchaseEvent event) {
        scoreboard.updateScoreboardFor(event.getPlayer());
    }
}
