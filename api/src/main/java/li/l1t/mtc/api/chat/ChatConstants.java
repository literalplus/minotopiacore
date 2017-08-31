/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package li.l1t.mtc.api.chat;

import li.l1t.common.chat.XyComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

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

    /**
     * @return a new component builder like {@link #MTC_PREFIX}
     */
    public static XyComponentBuilder prefixBuilder() {
        return new XyComponentBuilder("[", ChatColor.GRAY)
                .append("MTC", ChatColor.GOLD, ChatColor.BOLD)
                .append("]", ChatColor.GRAY, ComponentBuilder.FormatRetention.NONE)
                .append(" ", ChatColor.GOLD);
    }
}
