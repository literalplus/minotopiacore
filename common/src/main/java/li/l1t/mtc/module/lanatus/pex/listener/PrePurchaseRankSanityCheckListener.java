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

import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.shop.api.event.PrePurchaseEvent;
import li.l1t.mtc.api.chat.MessageType;
import li.l1t.mtc.module.lanatus.pex.product.PexProduct;
import li.l1t.mtc.module.lanatus.pex.product.PexProductRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Optional;

/**
 * Listens for pre purchase events for PEx products and checks the current rank of the buyer.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-25-11
 */
public class PrePurchaseRankSanityCheckListener extends AbstractPurchaseListener {
    public PrePurchaseRankSanityCheckListener(PexProductRepository pexProductRepository) {
        super(pexProductRepository);
    }

    @EventHandler
    public void onPrePurchase(PrePurchaseEvent evt) {
        if (isRelevantProduct(evt.getProduct())) {
            checkPexProduct(evt);
        }
    }

    private void checkPexProduct(PrePurchaseEvent evt) {
        Optional<PexProduct> pexProduct = findPexProduct(evt);
        if (pexProduct.isPresent()) {
            sanityCheckRank(evt, pexProduct.get());
        } else {
            denyPurchaseDueToMissingMetadata(evt);
        }
    }

    private void denyPurchaseDueToMissingMetadata(PrePurchaseEvent evt) {
        MessageType.INTERNAL_ERROR.sendTo(evt.getPlayer(),
                "Dieses Produkt (%s) hat keine Metadaten. Bitte wende dich an den Support.",
                evt.getProduct()
        );
        evt.setCancelled(true);
    }

    private void sanityCheckRank(PrePurchaseEvent evt, PexProduct product) {
        if(!checkRankChangeLegal(product, evt.getPlayer())) {
            evt.setCancelled(true);
        }
    }

    private boolean checkRankChangeLegal(PexProduct pexProduct, Player player) {
        if (pexProduct.getInitialRank() != null) {
            AccountSnapshot snapshot = accountRepository.findOrDefault(player.getUniqueId());
            if (!snapshot.getLastRank().equals(pexProduct.getInitialRank())) {
                MessageType.USER_ERROR.sendTo(player, "Du benötigst für dieses Produkt den Rang '%s'. Bitte erwirb zuerst diesen.", pexProduct.getInitialRank());
                return false;
            }
        }
        return true;
    }
}
