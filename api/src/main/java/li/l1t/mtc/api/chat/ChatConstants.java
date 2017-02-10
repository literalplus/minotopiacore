/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.api.chat;

import li.l1t.common.chat.XyComponentBuilder;
import net.md_5.bungee.api.ChatColor;

/**
 * Provides constant expressions commonly used in chat messages as public static final fields.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-08-23
 */
public class ChatConstants {
    public static final String MTC_PREFIX = "§8[§6§lMTC§8] §6";
    public static final String LEFT_ARROW = "➩";
    public static final String LEFT_HEADER = "»»»";
    public static final String RIGHT_HEADER = "«««";
    public static final ChatColor PRIMARY_COLOR = ChatColor.GOLD;
    public static final ChatColor SECONDARY_COLOR = ChatColor.GREEN;

    private ChatConstants() {

    }

    /**
     * Converts a message with custom format codes into a message with their replacements. Supports
     * the following:
     * <pre>
     *  - replaces §x with the {@link ChatConstants#MTC_PREFIX default MTC prefix}
     *  - replaces §p with the {@link ChatConstants#PRIMARY_COLOR default primary chat color}
     *  - replaces §s with the {@link ChatConstants#SECONDARY_COLOR default secondary chat color}
     * </pre>
     *
     * @param chatPattern the initial pattern to format
     * @return the formatted string
     */
    public static String convertCustomColorCodes(String chatPattern) {
        return chatPattern
                .replaceAll("§x", ChatConstants.MTC_PREFIX)
                .replaceAll("§p", ChatConstants.PRIMARY_COLOR.toString())
                .replaceAll("§s", ChatConstants.SECONDARY_COLOR.toString());
    }

    /**
     * @return a new component builder adhering to the specifications of {@link MessageType#RESULT_LINE}
     */
    public static XyComponentBuilder resultLineBuilder() {
        return new XyComponentBuilder("➩", ChatColor.YELLOW).bold(true)
                .append(" ", ChatColor.GOLD).bold(false);
    }

    /**
     * @return a new component builder adhering to the specifications of {@link MessageType#LIST_ITEM}
     */
    public static XyComponentBuilder listItemBuilder() {
        return new XyComponentBuilder("-➩", ChatColor.YELLOW).bold(true)
                .append(" ", ChatColor.GOLD).bold(false);
    }
}
