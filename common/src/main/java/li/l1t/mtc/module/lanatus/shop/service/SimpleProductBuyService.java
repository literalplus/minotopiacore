/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.service;

import li.l1t.common.exception.DatabaseException;
import li.l1t.lanatus.api.LanatusClient;
import li.l1t.lanatus.api.LanatusConnected;
import li.l1t.lanatus.api.exception.NoSuchProductException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.ProductBuyService;
import li.l1t.mtc.api.chat.MessageType;
import org.bukkit.entity.Player;

/**
 * A simple implemenation of a product buy service.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-18-11
 */
public class SimpleProductBuyService implements ProductBuyService, LanatusConnected {
    private final LanatusClient lanatus;

    public SimpleProductBuyService(LanatusClient lanatus) {
        this.lanatus = lanatus;
    }

    @Override
    public LanatusClient client() {
        return lanatus;
    }

    @Override
    public void attemptPurchase(Player player, Product product) {
        player.closeInventory();
        MessageType.RESULT_LINE.sendTo(player, "Versuche, %s für %s Melonen zu kaufen...", product, product.getMelonsCost());
        if (tryBuy(player, product)) {
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "%s erfolgreich gekauft.", product.getDisplayName());
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Du hast noch %d Melonen.", findCurrentMelonsCount(player));
        } else {
            MessageType.USER_ERROR.sendTo(player, "Es ist ein Fehler aufgetreten. Bitte versuche es später erneut.");
        }
    }

    private boolean tryBuy(Player player, Product product) {
        try {
            client().startPurchase(player.getUniqueId())
                    .withProduct(product)
                    .withComment("MTC Lanatus Shop GUI")
                    .build();
        } catch (NoSuchProductException e) {
            MessageType.INTERNAL_ERROR.sendTo(player, "Unbekanntes Produkt.");
            return false;
        } catch (DatabaseException e) {
            MessageType.INTERNAL_ERROR.sendTo(player, "Datenbankfehler.");
            return false;
        }
        return true;
    }

    @Override
    public int findCurrentMelonsCount(Player player) {
        return client().accounts().findOrDefault(player.getUniqueId()).getMelonsCount();
    }

}
