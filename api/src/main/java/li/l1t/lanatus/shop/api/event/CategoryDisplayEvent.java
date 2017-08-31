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

package li.l1t.lanatus.shop.api.event;

import li.l1t.common.util.PredicateHelper;
import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This event is fired when a category is viewed in the Lanatus shop.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-28-11
 */
public class CategoryDisplayEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Category category;
    private final Map<Product, ItemStack> productIconMap;
    private final AccountSnapshot account;

    public CategoryDisplayEvent(Player who, Category category, Collection<Product> products, AccountSnapshot account, Function<Product, ItemStack> iconFunction) {
        super(who);
        this.category = category;
        this.productIconMap = products.stream().collect(Collectors.toMap(Function.identity(), iconFunction));
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public Map<Product, ItemStack> getProductIconMap() {
        return productIconMap;
    }

    public void filterProducts(Predicate<Product> filter) {
        removeIf(PredicateHelper.not(filter));
    }

    public boolean removeIf(Predicate<Product> predicate) {
        return productIconMap.keySet().removeIf(predicate);
    }

    public void remapIf(Predicate<Product> filter, Function<Product, ItemStack> iconFunction) {
        productIconMap.keySet().stream()
                .filter(filter)
                .forEach(product -> productIconMap.replace(product, iconFunction.apply(product)));
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    public AccountSnapshot getAccount() {
        return account;
    }
}
