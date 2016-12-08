/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
