package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.ShopPriceCalculator;
import io.github.xxyy.mtc.module.shop.TransactionType;
import io.github.xxyy.mtc.module.shop.ui.util.ShopStringAdaptor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

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
     * Sends information about the price of a single piece of a shop item to a command sender. This handles null values
     * as untradable items.
     *
     * @param receiver  the receiver of the messages
     * @param item      the item to display, may be null
     * @param queryInfo an additional string describing the item, used when item is null, may be null
     */
    public void sendPriceInfo(CommandSender receiver, ShopItem item, String queryInfo) {
        if (!checkTradable(receiver, item, queryInfo)) {
            return;
        }

        if (item.canBeBought()) {
            ComponentSender.sendTo(
                    module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann für ", ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(item.getBuyCost()), ChatColor.YELLOW)
                            .append(" gekauft werden.", ChatColor.GOLD),
                    receiver
            );
        }
        if (item.canBeSold()) {
            ComponentSender.sendTo(
                    module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann für ", ChatColor.GOLD)
                            .append(ShopStringAdaptor.getCurrencyString(item.getSellWorth()), ChatColor.YELLOW)
                            .append(" verkauft werden.", ChatColor.GOLD),
                    receiver
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
}
