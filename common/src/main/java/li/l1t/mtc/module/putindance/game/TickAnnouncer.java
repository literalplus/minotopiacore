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

package li.l1t.mtc.module.putindance.game;

import li.l1t.mtc.hook.TitleManagerHook;
import li.l1t.mtc.module.putindance.PutinDanceModule;
import li.l1t.mtc.util.DyeColorConversions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Announces info related to ticks using the TitleManager plugin.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public class TickAnnouncer {
    private final TitleManagerHook hook;

    public TickAnnouncer(Plugin plugin) {
        hook = new TitleManagerHook(plugin);
    }

    public void announceSafeColor(DyeColor safeColor) {
        Bukkit.getOnlinePlayers().forEach(player -> announceSafeColorTo(player, safeColor));
    }

    private void announceSafeColorTo(Player player, DyeColor safeColor) {
        ChatColor chatColor = DyeColorConversions.chatColorFromDye(safeColor);
        String germanName = DyeColorConversions.germanNameFromDye(safeColor);
        if (hook.isActive()) {
            hook.sendTitle(player,
                    chatColor + germanName,
                    "§3sagt Putin"
            );
        } else {
            sendChatPrefixed(player, "Putin sagt: " + chatColor + germanName + "§3!");
        }
    }

    private void sendChatPrefixed(Player player, String format, Object... args) {
        player.sendMessage(PutinDanceModule.CHAT_PREFIX + String.format(format, args));
    }

    public void announceVodkaMode() {
        Bukkit.getOnlinePlayers().forEach(this::announceVodkaModeTo);
    }

    private void announceVodkaModeTo(Player player) {
        if (hook.isActive()) {
            hook.sendTitle(player,
                    "§eVodkamodus!",
                    "Jetzt fallen irgendwelche Blöcke weg!"
            );
        } else {
            sendChatPrefixed(player, "§e§lVodkamodus!§f Jetzt fallen irgendwelche Blöcke weg!");
        }
    }

    public void announceRemoval() {
        Bukkit.getOnlinePlayers().forEach(this::announceRemovalTo);
    }

    private void announceRemovalTo(Player player) {
        if (hook.isActive()) {
            hook.sendActionbarMessage(player, "§enom §cnom §anom");
        } else {
            sendChatPrefixed(player, "nom nom nom");
        }
    }
}
