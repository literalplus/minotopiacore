/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.pex.listener;

import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.event.CategoryDisplayEvent;
import li.l1t.mtc.module.lanatus.pex.LanatusPexModule;
import li.l1t.mtc.module.lanatus.pex.product.PexProduct;
import li.l1t.mtc.module.lanatus.pex.product.PexProductRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Listens for categories being displayed and filters out PEx products that the current user cannot transist to because
 * of their current rank.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-29-11
 */
public class ApplicableRanksFilterListener implements Listener {
    private final PexProductRepository pexProductRepository;

    public ApplicableRanksFilterListener(PexProductRepository pexProductRepository) {
        this.pexProductRepository = pexProductRepository;
    }

    @EventHandler
    public void onCategoryDisplay(CategoryDisplayEvent event) {
        event.filterProducts(showOnlyApplicableProducts(event.getAccount().getLastRank()));
    }

    @NotNull
    private Predicate<Product> showOnlyApplicableProducts(String currentRank) {
        return product -> product.getModule().equals(LanatusPexModule.LANATUS_MODULE_NAME) &&
                pexProductRepository.getByProduct(product)
                .map(PexProduct::getInitialRank)
                .map(currentRank::equals)
                .orElse(true);
    }
}
