/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
