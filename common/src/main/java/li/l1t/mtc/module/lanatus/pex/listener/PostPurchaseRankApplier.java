/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
        executePostPurchaseCommands(product);
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

    private void executePostPurchaseCommands(PexProduct product) {
        for(String command : product.getCommands()) {
            executePostPurchaseCommand(command);
        }
    }

    private void executePostPurchaseCommand(String command) {
        String rawCommand = stripCommandSlashIfPresent(command);
        server.dispatchCommand(server.getConsoleSender(), rawCommand);
    }

    private String stripCommandSlashIfPresent(String command) {
        return command.replaceFirst("^/", "");
    }
}
