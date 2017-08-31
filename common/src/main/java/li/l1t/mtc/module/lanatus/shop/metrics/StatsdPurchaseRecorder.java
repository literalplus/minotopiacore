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

package li.l1t.mtc.module.lanatus.shop.metrics;

import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.metrics.PurchaseRecorder;
import me.minotopia.statsd_bukkit.NonBlockingStatsDClient;
import net.md_5.bungee.api.ChatColor;
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
        return ChatColor.stripColor(insaneString).replaceAll("\\W", "_");
    }
}
