/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.lanatus.shop.api.event;

import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.shop.api.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final List<Product> products;
    private final AccountSnapshot account;

    public CategoryDisplayEvent(Player who, Category category, Collection<Product> products, AccountSnapshot account) {
        super(who);
        this.category = category;
        this.products = new ArrayList<>(products);
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
    }

    public void filterProducts(Predicate<Product> filter) {
        setProducts(products.stream().filter(filter).collect(Collectors.toList()));
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
