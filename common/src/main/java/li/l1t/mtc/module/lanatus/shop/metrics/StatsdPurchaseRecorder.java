/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.shop.metrics;

import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.metrics.PurchaseRecorder;
import me.minotopia.statsd_bukkit.NonBlockingStatsDClient;
import org.bukkit.entity.Player;

/**
 * Records purchase metrics to Statsd.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-21-11
 */
public class StatsdPurchaseRecorder implements PurchaseRecorder {
    private final NonBlockingStatsDClient statsd;

    public StatsdPurchaseRecorder(NonBlockingStatsDClient statsd) {
        this.statsd = statsd;
    }

    @Override
    public void handlePurchase(Player player, Product product) {
        statsd.increment("lanatus.purchases.all");
        statsd.increment("lanatus.purchases.label." + sanitise(product.getDisplayName()));
        statsd.increment("lanatus.purchases.uuid." + product.getUniqueId().toString());
        statsd.increment("lanatus.purchases.module." + sanitise(product.getModule()));
        statsd.count("lanatus.melons.spent", product.getMelonsCost());
    }

    private String sanitise(String insaneString) {
        return insaneString.replaceAll("\\W", "_");
    }
}
