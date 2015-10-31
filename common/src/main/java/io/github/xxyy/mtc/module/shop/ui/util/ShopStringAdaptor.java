package io.github.xxyy.mtc.module.shop.ui.util;

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
                throw new AssertionError("Unknown transaction type" + type);
        }
    }
}
