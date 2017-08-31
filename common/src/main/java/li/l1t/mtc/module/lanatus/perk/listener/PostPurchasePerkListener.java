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

package li.l1t.mtc.module.lanatus.perk.listener;

import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.event.PostPurchaseEvent;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.lanatus.perk.LanatusPerkModule;
import li.l1t.mtc.module.lanatus.perk.api.PerkRepository;
import li.l1t.mtc.module.lanatus.perk.repository.PerkMeta;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

/**
 * Listens for completed Lanatus purchases and grants access to purchased perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class PostPurchasePerkListener implements Listener {
    private static final Logger LOGGER = LogManager.getLogger(PostPurchasePerkListener.class);
    private final PerkRepository perkRepository;

    public PostPurchasePerkListener(PerkRepository perkRepository) {
        this.perkRepository = perkRepository;
    }

    @EventHandler
    public void onPostPurchase(PostPurchaseEvent event) {
        Product product = event.getProduct();
        if (product.getModule().equals(LanatusPerkModule.MODULE_NAME)) {
            Optional<PerkMeta> perkMeta = perkRepository.findByProductId(product.getUniqueId());
            if (perkMeta.isPresent()) {
                enablePerk(event, perkMeta.get());
                MessageType.RESULT_LINE_SUCCESS.sendTo(event.getPlayer(), "Tippe §p/perks§a, um deine Perks zu verwalten.");
            } else {
                LOGGER.warn(
                        "Unknown perk: {} at player {} (purchase {})",
                        product.getUniqueId(), event.getPlayer().getUniqueId(),
                        event.getPurchase().getUniqueId()
                );
            }
        }
    }

    private void enablePerk(PostPurchaseEvent event, PerkMeta meta) {
        perkRepository.makeAvailablePermanently(event.getPlayer().getUniqueId(), meta.getProductId());
    }
}
