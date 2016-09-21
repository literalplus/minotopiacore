/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.util;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;

/**
 * Utility class for converting DyeColor values into different other things.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class DyeColorConversions {
    private static final ImmutableMap<DyeColor, ChatColor> woolToChatColor = new ImmutableMap.Builder<DyeColor, ChatColor>()
            .put(DyeColor.WHITE, ChatColor.WHITE).put(DyeColor.ORANGE, ChatColor.GOLD)
            .put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE).put(DyeColor.LIGHT_BLUE, ChatColor.AQUA)
            .put(DyeColor.YELLOW, ChatColor.YELLOW).put(DyeColor.LIME, ChatColor.GREEN)
            .put(DyeColor.PINK, ChatColor.RED).put(DyeColor.GRAY, ChatColor.DARK_GRAY)
            .put(DyeColor.SILVER, ChatColor.GRAY).put(DyeColor.CYAN, ChatColor.DARK_AQUA)
            .put(DyeColor.PURPLE, ChatColor.DARK_PURPLE).put(DyeColor.BLUE, ChatColor.DARK_BLUE)
            .put(DyeColor.BROWN, ChatColor.BLUE).put(DyeColor.GREEN, ChatColor.DARK_GREEN)
            .put(DyeColor.RED, ChatColor.DARK_RED).put(DyeColor.BLACK, ChatColor.BLACK).build();
    private static final ImmutableMap<DyeColor, String> woolToGerman = new ImmutableMap.Builder<DyeColor, String>()
            .put(DyeColor.WHITE, "weiss").put(DyeColor.ORANGE, "orange")
            .put(DyeColor.MAGENTA, "magenta").put(DyeColor.LIGHT_BLUE, "hellblau")
            .put(DyeColor.YELLOW, "gelb").put(DyeColor.LIME, "hellgrün")
            .put(DyeColor.PINK, "rosa").put(DyeColor.GRAY, "dunkelgrau")
            .put(DyeColor.SILVER, "hellgrau").put(DyeColor.CYAN, "cyan")
            .put(DyeColor.PURPLE, "lila").put(DyeColor.BLUE, "blau")
            .put(DyeColor.BROWN, "braun").put(DyeColor.GREEN, "dunkelgrün")
            .put(DyeColor.RED, "rot").put(DyeColor.BLACK, "schwarz").build();

    private DyeColorConversions() {

    }

    public static ChatColor chatColorFromDye(DyeColor dyeColor) {
        return getOrFail(woolToChatColor, dyeColor, "ChatColor");
    }

    public static String germanNameFromDye(DyeColor dyeColor) {
        return getOrFail(woolToGerman, dyeColor, "german name");
    }

    private static <T> T getOrFail(ImmutableMap<DyeColor, T> source, DyeColor color, String type) {
        if (!source.containsKey(color)) {
            throw new IllegalArgumentException("no " + type + " for dye color " + color);
        }
        return source.get(color);
    }
}
