package io.github.xxyy.mtc.module.shop.ui.text;

import com.google.common.base.Joiner;
import io.github.xxyy.mtc.module.shop.ShopItem;
import io.github.xxyy.mtc.module.shop.ShopModule;
import io.github.xxyy.mtc.module.shop.api.ShopItemManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SearchShopAction extends AbstractShopAction {
    private static final Joiner SPACE_JOINER = Joiner.on(' ');

    private ShopItemManager itemManager;
    private ShopTextOutput output;
    private Map<String, ShopItem> itemAliases;

    protected SearchShopAction(ShopModule module) {
        super("shop", "suchen", 1, null, "search", "s");
        itemManager = module.getItemManager();
        output = module.getTextOutput();
        itemAliases = itemManager.getItemAliases();
    }

    @Override
    public void execute(String[] args, Player plr, String label) {
        String search = SPACE_JOINER.join(args);
        ShopItem item = itemManager.getItem(search);
        if (item != null) {
            output.sendPriceInfo(plr, item, 1, "§e\"" + search + "\"§6");
            return;
        }
        Set<ShopItem> matches = matchShopItems(search);
        //TODO display results
    }

    public Set<ShopItem> matchShopItems(String search) {
        Set<ShopItem> matches = new LinkedHashSet<>();
        Set<ShopItem> matchesMaybe = new LinkedHashSet<>();

        if (search.contains(":")) {
            String[] split = search.split(":");
            search = split[0];
        }
        search = search.trim();
        if (search.isEmpty()) {
            return matches;
        }

        String lsearch = search.toLowerCase();
        for (Map.Entry<String, ShopItem> entry : itemAliases.entrySet()) {
            String alias = entry.getKey();
            ShopItem shopItem = entry.getValue();

            String lalias = alias.toLowerCase();

            if (lalias.startsWith(lsearch)
                || lsearch.length() > 3 && getEqualityPercentage(lalias, lsearch) > .7) { //TODO test out similarity percentage and min threshold
                matches.add(shopItem);
            }
            if (lsearch.startsWith(lalias)) { //TODO should we use this check, as a specialised search matches a more general item name?
                matchesMaybe.add(shopItem);
            }
        }
        matches.addAll(matchesMaybe);
        return matches;
    }

    /**
     * Calculates similarity percentage based on {@link StringUtils#getLevenshteinDistance(CharSequence, CharSequence)}
     * <br><br>
     * Its adviced to compare the strings using {@link String#equals(Object)} or {@link String#equalsIgnoreCase(String)}
     * before calling this method, as the levensthein distance calculation is quite heavy (still fast, but)
     *
     * @param s1 the first string (prefer the longer one if you know)
     * @param s2 the second string
     * @return the relative similarity, from 0 (no similarity) up to 1 (equal)
     */
    @Nonnegative
    public static double getEqualityPercentage(@NotNull String s1, @NotNull String s2) {
        //adapted from http://stackoverflow.com/a/16018452 (removed one variable, as unneeded)
        if (s1.length() < s2.length()) {
            String s1copy = s1;
            s1 = s2;
            s2 = s1copy;
        }
        int longerLength = s1.length();
        if (longerLength == 0) { /* both strings are zero length */
            return 1.0D;
        }
        return (longerLength - StringUtils.getLevenshteinDistance(s1, s2)) / (double) longerLength;
    }

    @Override
    public void sendHelpLines(Player plr) {
        sendHelpLine(plr, "<Suchbegriff>", "Sucht nach Items mit bestimmtem Namen");
    }
}
