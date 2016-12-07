/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.lanatus.perk.ui.inventory;

import li.l1t.common.inventory.gui.PaginationListMenu;
import li.l1t.mtc.module.lanatus.perk.api.Perk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An inventory menu that allows players to select from a list of perks.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-07
 */
public class MyPerksMenu extends PaginationListMenu<Perk> {
    private final BiConsumer<Perk, InventoryClickEvent> clickHandler;
    private final Function<Perk, ItemStack> iconFunction;

    public MyPerksMenu(Player player, Plugin plugin, BiConsumer<Perk, InventoryClickEvent> clickHandler,
                       Function<Perk, ItemStack> iconFunction) {
        super(plugin, player);
        this.clickHandler = clickHandler;
        this.iconFunction = iconFunction;
    }

    @Override
    protected void handleValueClick(Perk perk, InventoryClickEvent inventoryClickEvent) {
        clickHandler.accept(perk, inventoryClickEvent);
    }

    @Override
    protected ItemStack drawItem(Perk perk) {
        return iconFunction.apply(perk);
    }

    @Override
    protected String formatTitle(int currentPageNum, int pageCount) {
        return String.format("§9MinoTopia Perks (%d/%d)", currentPageNum, pageCount);
    }
}
