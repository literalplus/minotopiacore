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

package li.l1t.mtc.module.lanatus.pex.listener;

import li.l1t.common.exception.InternalException;
import li.l1t.common.exception.NonSensitiveException;
import li.l1t.lanatus.api.account.MutableAccount;
import li.l1t.lanatus.api.exception.AccountConflictException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.lanatus.pex.product.PexProduct;
import li.l1t.mtc.module.lanatus.pex.product.PexProductRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * A listener that handles post purchase events sent by the Lanatus shop and applies the purchased rank to a player.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class PostPurchaseRankApplier extends AbstractPurchaseListener {
    private final Server server;

    public PostPurchaseRankApplier(PexProductRepository pexProductRepository, Server server) {
        super(pexProductRepository);
        this.server = server;
    }

    @EventHandler
    public void onPostPurchase(PostPurchaseEvent evt) {
        Product product = evt.getPurchase().getProduct();
        if (isRelevantProduct(product)) {
            tryApplyPexProduct(evt);
        }
    }

    private void tryApplyPexProduct(PostPurchaseEvent evt) {
        try {
            applyPexProduct(evt);
        } catch (NonSensitiveException e) {
            e.printStackTrace();
            evt.getPlayer().sendMessage(e.getColoredMessage());
        }
    }

    private void applyPexProduct(PostPurchaseEvent evt) {
        applyProduct(evt.getPlayer(), getPexProductOrFail(evt));
    }

    private void applyProduct(Player player, PexProduct product) {
        setRankFor(player, product.getTargetRank());
        executePostPurchaseCommands(product, player);
    }

    private void setRankFor(Player player, String targetRank) {
        MutableAccount account = accountRepository.findMutable(player.getUniqueId());
        account.setLastRank(targetRank);
        trySave(account, false);
        MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Dein Rang ist jetzt §p%s§s.", targetRank);
        MessageType.WARNING.sendTo(player, "Du musst dich neu einloggen, bevor der  Rang verfügbar ist.");
    }

    private void trySave(MutableAccount account, boolean isRetry) {
        try {
            accountRepository.save(account);
        } catch (AccountConflictException e) {
            if (isRetry) {
                throw new InternalException("Konnte deinen Account nicht speichern!");
            } else {
                trySave(account, true);
            }
        }
    }

    private void executePostPurchaseCommands(PexProduct product, Player player) {
        for (String command : product.getCommands()) {
            executePostPurchaseCommand(command, product, player);
        }
    }

    private void executePostPurchaseCommand(String command, PexProduct product, Player player) {
        String rawCommand = stripCommandSlashIfPresent(command);
        server.dispatchCommand(server.getConsoleSender(), substituteTemplates(rawCommand, product, player));
    }

    private String substituteTemplates(String rawCommand, PexProduct product, Player player) {
        return rawCommand
                .replaceAll("%p", player.getName())
                .replaceAll("%u", player.getUniqueId().toString())
                .replaceAll("%r", product.getTargetRank())
                .replaceAll("%c", product.getInitialRank());
    }

    private String stripCommandSlashIfPresent(String command) {
        return command.replaceFirst("^/", "");
    }
}
