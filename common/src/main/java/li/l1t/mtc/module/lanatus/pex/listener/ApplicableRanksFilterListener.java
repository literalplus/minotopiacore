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
        return product -> !product.getModule().equals(LanatusPexModule.LANATUS_MODULE_NAME) ||
                pexProductRepository.getByProduct(product)
                .map(PexProduct::getInitialRank)
                .map(currentRank::equals)
                .orElse(true);
    }
}
