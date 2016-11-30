/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
