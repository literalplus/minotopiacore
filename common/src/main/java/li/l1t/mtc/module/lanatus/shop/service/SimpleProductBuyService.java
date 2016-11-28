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
import li.l1t.lanatus.api.builder.PurchaseBuilder;
import li.l1t.lanatus.api.exception.NoSuchProductException;
import li.l1t.lanatus.api.exception.NotEnoughMelonsException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.lanatus.shop.api.ProductBuyService;
import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.lanatus.shop.api.event.PrePurchaseEvent;
import li.l1t.lanatus.shop.api.metrics.DummyPurchaseRecorder;
import li.l1t.lanatus.shop.api.metrics.PurchaseRecorder;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.api.module.inject.InjectMe;
import li.l1t.mtc.module.lanatus.base.MTCLanatusClient;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * A simple implemenation of a product buy service.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-18-11
 */
public class SimpleProductBuyService implements ProductBuyService, LanatusConnected {
    private final PluginManager pluginManager;
    private final LanatusClient lanatus;
    private PurchaseRecorder purchaseRecorder = new DummyPurchaseRecorder();

    @InjectMe
    public SimpleProductBuyService(MTCPlugin plugin, MTCLanatusClient lanatus) {
        this.pluginManager = plugin.getServer().getPluginManager();
        this.lanatus = lanatus;
    }

    @Override
    public LanatusClient client() {
        return lanatus;
    }

    @Override
    public void attemptPurchase(Player player, Product product) {
        player.closeInventory();
        MessageType.RESULT_LINE.sendTo(player, "Versuche, %s§p für %s Melonen zu kaufen...", product.getDisplayName(), product.getMelonsCost());
        if (tryBuy(player, product)) {
            purchaseRecorder.handlePurchase(player, product);
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "%s§p erfolgreich gekauft.", product.getDisplayName());
            MessageType.RESULT_LINE_SUCCESS.sendTo(player, "Du hast noch %d Melonen.", findCurrentMelonsCount(player));
        }
    }

    private boolean tryBuy(Player player, Product product) {
        try {
            PurchaseBuilder purchaseBuilder = client().startPurchase(player.getUniqueId())
                    .withProduct(product)
                    .withComment("MTC Lanatus Shop GUI");
            if (callPrePurchaseEvent(player, product, purchaseBuilder).isCancelled()) {
                MessageType.USER_ERROR.sendTo(player, "Kauf fehlgeschlagen.");
                return false;
            }
            purchaseBuilder.build();
            callPostPurchaseEvent(player, purchaseBuilder.getPurchase());
        } catch (NoSuchProductException e) {
            MessageType.INTERNAL_ERROR.sendTo(player, "Unbekanntes Produkt.");
            return false;
        } catch (DatabaseException e) {
            MessageType.INTERNAL_ERROR.sendTo(player, "Datenbankfehler. Bitte versuche es später erneut.");
            return false;
        } catch (NotEnoughMelonsException e) {
            MessageType.USER_ERROR.sendTo(player, e.getMessage());
            return false;
        } catch (Exception e) {
            MessageType.INTERNAL_ERROR.sendTo(player, "Interner Fehler. Bitte versuche es später erneut.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private PrePurchaseEvent callPrePurchaseEvent(Player player, Product product, PurchaseBuilder builder) {
        PrePurchaseEvent event = new PrePurchaseEvent(player, builder, product);
        pluginManager.callEvent(event);
        return event;
    }

    private PostPurchaseEvent callPostPurchaseEvent(Player player, Purchase purchase) {
        PostPurchaseEvent event = new PostPurchaseEvent(player, purchase);
        pluginManager.callEvent(event);
        return event;
    }

    @Override
    public int findCurrentMelonsCount(Player player) {
        return client().accounts().findOrDefault(player.getUniqueId()).getMelonsCount();
    }

    @Override
    public void setPurchaseRecorder(PurchaseRecorder purchaseRecorder) {
        this.purchaseRecorder = purchaseRecorder;
    }
}
