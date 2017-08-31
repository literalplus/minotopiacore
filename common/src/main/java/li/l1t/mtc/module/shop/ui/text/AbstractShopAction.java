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

package li.l1t.mtc.module.shop.ui.text;

import li.l1t.common.chat.XyComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides abstract base functionality for shop actions.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
public abstract class AbstractShopAction implements ShopAction {
    private final Set<String> aliases = new LinkedHashSet<>();
    private final int minimumArguments;
    private final String displayName;
    private final String baseCommand;
    private final String permission;

    protected AbstractShopAction(String baseCommand, String displayName, int minimumArguments, String permission, String... aliases) {
        this.baseCommand = baseCommand;
        this.displayName = displayName;
        this.minimumArguments = minimumArguments;
        this.permission = permission;
        this.aliases.add(displayName);
        Collections.addAll(this.aliases, aliases);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean matches(String actionName) {
        actionName = actionName.toLowerCase();
        for (String alias : aliases) {
            if (alias.toLowerCase().equals(actionName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean fuzzyMatches(String actionName) {
        actionName = actionName.toLowerCase();
        for (String alias : aliases) {
            if (alias.toLowerCase().startsWith(actionName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getMinimumArguments() {
        return minimumArguments;
    }

    protected void sendHelpLine(Player plr, String arguments, String description) {
        String command = "/" + baseCommand + " " + displayName + " ";
        plr.spigot().sendMessage(
                new XyComponentBuilder(command + arguments + " ", ChatColor.DARK_AQUA)
                        .tooltip("§eKlicken zum Kopieren")
                        .suggest(command)
                        .append(description, ChatColor.GRAY)
                        .create()
        );
    }

    @Override
    public String getPermission() {
        return permission;
    }
}
