package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * Handles text output for the shop module. Stateless.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-10-28
 */
public class ShopTextOutput {
    private final ShopModule module;
    private final ShopPriceCalculator calculator;

    public ShopTextOutput(ShopModule module) {
        this.module = module;
        calculator = new ShopPriceCalculator(module.getItemManager());
    }

    /**
     * Sends a message to a command sender prefixed with the associated module's chat prefix.
     *
     * @param receiver   the receiver of the message
     * @param legacyText the legacy text to appear after the prefix
     */
    public void sendPrefixed(CommandSender receiver, String legacyText) {
        receiver.sendMessage(module.getChatPrefix() + legacyText);
    }

    /**
     * Checks whether a passed item is non-null and tradable. Null values are recognised as untradable. If the item is
     * not tradable, sends an informational message to passed command sender.
     *
     * @param receiver  the receiver of the possible message
     * @param item      the item to check
     * @param queryInfo an additional string describing the item, used when untradable, may be null
     * @return whether the item is tradable
     */
    public boolean checkTradable(CommandSender receiver, ShopItem item, String queryInfo) {
        return checkTradable(receiver, item, queryInfo, null);
    }

    /**
     * Checks whether a passed item is non-null and tradable for a given transaction type. Null values are recognised
     * as untradable. If the item is not tradable, sends an informational message to passed command sender.
     *
     * @param receiver  the receiver of the possible message
     * @param item      the item to check
     * @param queryInfo an additional string describing the item, used when untradable, may be null
     * @param type      the transaction type to check tradableness for
     * @return whether the item is tradable
     */
    public boolean checkTradable(CommandSender receiver, ShopItem item, String queryInfo, TransactionType type) {
        boolean tradable = type != null ? type.isTradable(item) : item.canBeBought() || item.canBeSold();
        if (!tradable) {
            String itemSpecifier = queryInfo == null ?
                    "Dieses Item" :
                    "Das Item " + queryInfo;

            sendPrefixed(receiver, itemSpecifier + " kann nicht " +
                    ShopStringAdaptor.getParticipleII(type) + " werden.");
            return false;
        }
        return true;
    }

    /**
     * This method does not replace {@link #checkTradable(CommandSender, ShopItem, String)} or {@link #checkTradable(CommandSender, ShopItem, String, TransactionType)}, its an additional check
     *
     * @return whether selling that specific itemStack is forbidden
     * @see {@link io.github.xxyy.mtc.module.shop.api.ShopItemManager#isTradeProhibited(ItemStack)}
     */
    public boolean extraCheckStackSellable(CommandSender reciever, ItemStack itemStack) {
        if (module.getItemManager().isTradeProhibited(itemStack)) {
            sendPrefixed(reciever, "§cDieses Item kann nicht verkauft werden.");
            return false;
        }
        return true;
    }

    /**
     * This method does not replace {@link #checkTradable(CommandSender, ShopItem, String)} or {@link #checkTradable(CommandSender, ShopItem, String, TransactionType)}, its an additional check
     *
     * @return whether selling that specific itemStack is forbidden
     * @see {@link io.github.xxyy.mtc.module.shop.api.ShopItemManager#isTradeProhibited(ItemStack)}
     */
    public boolean extraCheckStackSellable(CommandSender reciever, ItemStack itemStack, String queryInfo) {
        if (module.getItemManager().isTradeProhibited(itemStack)) {
            sendPrefixed(reciever, "§cDas Item " + queryInfo + " kann nicht verkauft werden.");
            return false;
        }
        return true;
    }

    /**
     * Sends information about the price of specified amount of shop items. If amount is 1, sends only the price for
     * a single item. Otherwise, sends the price for specified amount of items too.
     *
     * @param receiver  the receiver of the messages
     * @param item      the item to display, may be null
     * @param amount    the amount of the item to display
     * @param queryInfo an additional string describing the item, used when item is null, may be null
     */
    public void sendPriceInfo(CommandSender receiver, ShopItem item, int amount, String queryInfo) {
        if (!checkTradable(receiver, item, queryInfo)) {
            return;
        }

        sendPriceInfoSingle(receiver, item, queryInfo);

        if (amount != 1) {
            sendPriceInfoMultiple(receiver, item, amount, queryInfo);
        }
    }

    /**
     * Sends information about the price of a single piece of a shop item to a command sender. This handles null values
     * as untradable items.
     *
     * @param receiver  the receiver of the messages
     * @param item      the item to display, may be null
     * @param queryInfo an additional string describing the item, used when item is null, may be null
     */
    public void sendPriceInfoSingle(CommandSender receiver, ShopItem item, String queryInfo) {
        if (!checkTradable(receiver, item, queryInfo)) {
            return;
        }

        if (item.canBeBought()) {
            XyComponentBuilder builder = module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                .append(" kann für ", ChatColor.GOLD)
                .append(ShopStringAdaptor.getCurrencyString(item.getBuyCostFinal()), ChatColor.YELLOW);
            if (item.isOnSale()) {
                builder.append(" statt ", ChatColor.GOLD)
                    .append(ShopStringAdaptor.getCurrencyString(item.getBuyCost()));
            }
            builder.append(" gekauft werden.", ChatColor.GOLD);
            ComponentSender.sendTo(builder, receiver);
        }
        if (item.canBeSold()) {
            XyComponentBuilder builder = module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                .append(" kann für ", ChatColor.GOLD)
                .append(ShopStringAdaptor.getCurrencyString(item.getSellWorth()), ChatColor.YELLOW)
                .append(" verkauft werden.", ChatColor.GOLD);
            ComponentSender.sendTo(builder, receiver);
        }
    }

    /**
     * Sends information about the price of a specific amount of a specific shop item to a command sender. This handles
     * null values as untradeable items.
     *
     * @param receiver  the receiver of the messages
     * @param item      the item to display, may be null
     * @param amount    the amount of the item to display
     * @param queryInfo an additional string describing the item, used when item is null, may be null
     */
    public void sendPriceInfoMultiple(CommandSender receiver, ShopItem item, int amount, String queryInfo) {
        if (!checkTradable(receiver, item, queryInfo)) {
            return;
        }

        String displayName = ShopStringAdaptor.getAdjustedDisplayName(item, amount);

        if (item.canBeBought()) {
            double finalPrice = calculator.calculatePrice(item, amount, TransactionType.BUY);
            ComponentSender.sendTo(
                    module.getPrefixBuilder()
                            .append(displayName, ChatColor.YELLOW)
                            .append((amount == 1 ? " kostet " : " kosten "), ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(finalPrice), ChatColor.YELLOW)
                            .append(". ", ChatColor.GOLD)
                            .append("[" + displayName + " kaufen]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                            .hintedCommand("/shop kaufen " + item.getSerializationName() + " " + amount)
                            .create(), receiver
            );
        }

        if (item.canBeSold()) {
            double finalPrice = calculator.calculatePrice(item, amount, TransactionType.SELL);
            ComponentSender.sendTo(
                    module.getPrefixBuilder()
                            .append(displayName, ChatColor.YELLOW)
                            .append((amount == 1 ? " ist " : " sind "), ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(finalPrice), ChatColor.YELLOW)
                            .append(" wert. ", ChatColor.GOLD)
                            .append("[" + displayName + " verkaufen]", ChatColor.DARK_GREEN, ChatColor.UNDERLINE)
                            .hintedCommand("/shop verkaufen " + item.getSerializationName() + " " + amount)
                            .create(), receiver
            );
        }
    }

    /**
     * Notifies a command sender about a successful transaction initiated by them with information about what exactly
     * happened.
     *
     * @param receiver the receiver of the message
     * @param item     the item involved in the transaction
     * @param amount   the amount of the item that was transferred
     * @param type     the type of the transaction
     */
    public void sendTransactionSuccess(CommandSender receiver, ShopItem item, int amount, TransactionType type) {
        ComponentSender.sendTo(
                module.getPrefixBuilder()
                        .append("Du hast erfolgreich ", ChatColor.GOLD)
                        .append(ShopStringAdaptor.getAdjustedDisplayName(item, amount), ChatColor.YELLOW)
                        .append(" für ", ChatColor.GOLD)
                        .append(ShopStringAdaptor.getCurrencyString(calculator.calculatePrice(item, amount, type)), ChatColor.YELLOW)
                        .append(" " + ShopStringAdaptor.getParticipleII(type), ChatColor.GOLD)
                        .create(), receiver
        );
    }

    /**
     * Notifies a command sender about a failed transaction initiated by them.
     *
     * @param receiver     the receiver of the message
     * @param type         the type of the transaction that failed
     * @param errorMessage the error message
     */
    public void sendTransactionFailure(CommandSender receiver, TransactionType type, String errorMessage) {
        ComponentSender.sendTo(
                module.getPrefixBuilder()
                        .append("Konnte nicht " + ShopStringAdaptor.getInfinitive(type) + ": ", ChatColor.RED)
                        .append(errorMessage, ChatColor.RED, ChatColor.ITALIC)
                        .create(),
                receiver);
    }

    /**
     * Notifies a command sender about an item being on sale.
     *
     * @param item the item currently on sale
     * @param reciever the player to send the item on sale
     */
    public void sendItemSale(ShopItem item, CommandSender reciever) {
        ComponentSender.sendTo(getItemSaleMessage(item), reciever);
    }

    /**
     * Broadcast notification about an item being on sale.
     * @param item the item currently on sale.
     */
    public void broadcastItemSale(ShopItem item) {
        BaseComponent[] itemSaleMessage = getItemSaleMessage(item);
        module.getPlugin().getServer().getOnlinePlayers().forEach(plr -> plr.spigot().sendMessage(itemSaleMessage));
    }

    private BaseComponent[] getItemSaleMessage(ShopItem item) {
        return module.getPrefixBuilder()
            .append("Neues Sonderangebot! ")
            .append(item.getDisplayName(), ChatColor.YELLOW)
            .append(" jetzt um ", ChatColor.GOLD)
            .append(formatPercentage(item.getSaleReductionPercent()), ChatColor.YELLOW)
            .append(" reduziert.", ChatColor.GOLD)
            .create();
    }

    public String formatPercentage(double percentage) {
        percentage *= 100;
        return Integer.toString((int) percentage) + '%';
    }
}
