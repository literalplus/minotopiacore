package io.github.xxyy.mtc.module.shop.ui.util;

import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.TransactionType;

/**
 * Adapts strings to different arguments, for proper grammar. This class aims to prevent things like "1 items" from
 * happening since these look unprofessional. Static utility class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-10-31
 */
public class ShopStringAdaptor {
    public static final String CURRENCY_SINGULAR = "MineCoin";
    public static final String CURRENCY_PLURAL = "MineCoins";

    private ShopStringAdaptor() {

    }

    /**
     * Returns a human-readable representation of given currency value. This respects singular and plural forms.
     *
     * @param amount the amount to format
     * @return a human-readable representation of given value, including currency name
     */
    public static String getCurrencyString(double amount) {
        return amount == 1 ?
                "einen " + CURRENCY_SINGULAR :
                amount + CURRENCY_PLURAL;
    }

    /**
     * Gets the human-readable display string of given item with given amount.
     *
     * @param item   the item to format
     * @param amount the amount to format
     * @return the human-readable display string
     */
    public static String getAdjustedDisplayName(ShopItem item, int amount) {
        return amount + " " + item;
    }

    /**
     * Returns a human-readable verb representation of given transaction type as a Participle II.
     *
     * @param type the type to get the Participle II for
     * @return a human-readable Participle II representation of given type
     */
    public static String getParticipleII(TransactionType type) {
        if (type == null) {
            return "gehandelt";
        }

        switch (type) {
            case SELL:
                return "verkauft";
            case BUY:
                return "gekauft";
            default:
                throw new AssertionError("Unknown transaction type " + type);
        }
    }

    /**
     * Returns a human-readable verb representation of given transaction type as Infinitive.
     *
     * @param type the type to get the Infinitive for
     * @return a human-readable Infinitive representation of given type
     */
    public static String getInfinitive(TransactionType type) {
        if (type == null) {
            return "handeln";
        }

        switch (type) {
            case SELL:
                return "verkaufen";
            case BUY:
                return "kaufen";
            default:
                throw new AssertionError("Unknown transaction type " + type);
        }
    }
}
