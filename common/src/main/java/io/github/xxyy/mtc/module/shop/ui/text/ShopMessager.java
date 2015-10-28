package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.chat.ComponentSender;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Provides user output for common messages used by the shop module.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-10-28
 */
public class ShopMessager {
    public static final String CURRENCY_SINGULAR = "MineCoin";
    public static final String CURRENCY_PLURAL = "MineCoins";
    private final ShopModule module;

    public ShopMessager(ShopModule module) {
        this.module = module;
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
        if (item == null || (!item.canBeSold() && !item.canBeBought())) {
            if (queryInfo == null) {
                sendPrefixed(receiver, "Dieses Item kann nicht gehandelt werden.");
            } else {
                sendPrefixed(receiver, "Das Item " + queryInfo + " kann nicht gehandelt werden.");
            }
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
                            .append(getCurrencyString(item.getBuyCost()), ChatColor.YELLOW)
                            .append(" gekauft werden.", ChatColor.GOLD),
                    receiver
            );
        }
        if (item.canBeSold()) {
            ComponentSender.sendTo(
                    module.getPrefixBuilder().append(item.getDisplayName(), ChatColor.YELLOW)
                            .append(" kann für ", ChatColor.GOLD)
                            .append(getCurrencyString(item.getSellWorth()), ChatColor.YELLOW)
                            .append(" verkauft werden.", ChatColor.GOLD),
                    receiver
            );
        }
    }

    /**
     * Returns a human-readable representation of given currency value. This respects singular and plural forms.
     *
     * @param amount the amount to format
     * @return a human-readbale representation of given value, including currency name
     */
    public String getCurrencyString(double amount) {
        return amount == 1 ?
                "einen " + CURRENCY_SINGULAR :
                amount + CURRENCY_PLURAL;
    }
}
