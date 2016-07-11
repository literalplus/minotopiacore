package io.github.xxyy.mtc.module.shop.ui.text;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.common.chat.XyComponentBuilder;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public boolean checkTradable(@Nonnull CommandSender receiver, @Nullable ShopItem item, @Nullable String queryInfo) {
        return checkTradable(receiver, item, queryInfo, null);
    }

    /**
     * Checks whether a passed item is non-null and tradable for a given transaction type. Null values are recognised
     * as untradable. If the item is not tradable, sends an informational message to passed command sender.
     *
     * @param receiver  the receiver of the possible message
     * @param item      the item to check
     * @param queryInfo an additional string describing the item, used when untradable, may be null
     * @param type      the transaction type to check for, or null to check if any trade is possible
     * @return whether the item is tradable
     */
    public boolean checkTradable(@Nonnull CommandSender receiver, @Nullable ShopItem item, @Nullable String queryInfo,
                                 @Nullable TransactionType type) {
        boolean tradable = item != null &&
                (type != null ? type.isTradable(item) : item.canBeBought() || item.canBeSold());
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
     * Checks whether given item is null, and issues a warning message to given receiver stating that such item does not
     * exist if so. Does nothing if given item is non-null.
     *
     * @param receiver  the receiver of the message
     * @param item      the item to check for nullity
     * @param queryInfo an additional string describing this item, used if null, may be null itself
     * @return whether given item is null
     */
    public boolean checkNonExistant(@Nonnull CommandSender receiver, @Nullable ShopItem item, @Nullable String queryInfo) {
        if (item == null) {
            String itemSpecifier = queryInfo == null ?
                    "Dieses Item" :
                    "Das Item " + queryInfo;

            sendPrefixed(receiver, itemSpecifier + " ist nicht im Shop vorhanden.");
            return true;
        }
        return false;
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

        receiver.sendMessage("§e »»»  Shopitem: " + item.getDisplayName() + "  «««");

        if (item.canBeBought()) {
            XyComponentBuilder builder = module.getPrefixBuilder()
                    .append("Kaufpreis: ", ChatColor.GOLD)
                    .append(ShopStringAdaptor.getCurrencyString(item.getManager().getBuyCost(item)), ChatColor.YELLOW);
            if (item.getManager().getDiscountManager().isDiscounted(item)) {
                builder.append(" statt ", ChatColor.GOLD)
                        .append(ShopStringAdaptor.getCurrencyString(item.getBuyCost()),
                                ChatColor.YELLOW, ChatColor.STRIKETHROUGH);
            }
            builder.append(" ", ComponentBuilder.FormatRetention.NONE)
                    .append("[kaufen...]", ChatColor.GREEN, ChatColor.UNDERLINE)
                    .hintedCommand("/shop buy " + item.getSerializationName());
            ComponentSender.sendTo(builder, receiver);
        }
        if (item.canBeSold()) {
            XyComponentBuilder builder = module.getPrefixBuilder()
                    .append("Verkaufspreis: ", ChatColor.GOLD)
                    .append(ShopStringAdaptor.getCurrencyString(item.getManager().getSellWorth(item)), ChatColor.YELLOW)
                    .append(" ", ComponentBuilder.FormatRetention.NONE)
                    .append("[verkaufen...]", ChatColor.GREEN, ChatColor.UNDERLINE)
                    .hintedCommand("/shop sell");
            ComponentSender.sendTo(builder, receiver);
        }
        if (item.getAliases().size() > 1) {
            receiver.sendMessage(String.format("%sAuch bekannt als: §e%s",
                    module.getChatPrefix(),
                    Joiner.on("§6, §e").join(item.getAliases())));
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
                        .append("Du hast ", ChatColor.GOLD)
                        .append(ShopStringAdaptor.getAdjustedDisplayName(item, amount), ChatColor.YELLOW)
                        .append(" für ", ChatColor.GOLD)
                        .append(ShopStringAdaptor.getCurrencyString(calculator.calculatePrice(item, amount, type)), ChatColor.YELLOW)
                        .append(" " + ShopStringAdaptor.getParticipleII(type) + ".", ChatColor.GOLD)
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
     * Notifies all online players and the console command sender of a new item being discounted.
     *
     * @param item the newly discounted item
     */
    public void announceDiscount(ShopItem item) {
        Preconditions.checkArgument(item.isDiscountable(), "item must be discountable");
        BaseComponent[] notification = module.getPrefixBuilder()
                .append("Jetzt billiger: ", ChatColor.GOLD, ChatColor.BOLD)
                .append(item.getDisplayName(), ChatColor.YELLOW).bold(false)
                .append(" um ", ChatColor.GOLD)
                .append(item.getDiscountPercentage() + "%", ChatColor.YELLOW)
                .append(" reduziert!", ChatColor.GOLD)
                .create();
        module.getPlugin().getServer().getOnlinePlayers().forEach(plr -> plr.spigot().sendMessage(notification));
        //This spams the console, so nah:
        //ComponentSender.sendTo(notification, module.getPlugin().getServer().getConsoleSender());
    }

    /**
     * Creates a hover tooltip info for a shop item.
     *
     * @param item the item to show info for
     * @return a hover event providing information about given item
     */
    public HoverEvent createItemHover(@Nullable ShopItem item) {
        if (item == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new XyComponentBuilder("Unbekanntes Item", ChatColor.RED).create());
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new XyComponentBuilder("Item: ", ChatColor.GOLD)
                        .append(item.getDisplayName(), ChatColor.YELLOW)
                        .append("\nKaufen: ", ChatColor.GOLD)
                        .append(item.canBeBought() ? item.getBuyCost() : "nein", ChatColor.YELLOW)
                        .append("\nVerkaufen: ", ChatColor.GOLD)
                        .append(item.canBeSold() ? item.getSellWorth() : "nein", ChatColor.YELLOW)
                        .create()
        );
    }
}
