package io.github.xxyy.mtc.module.shop.ui.text;

import io.github.xxyy.common.chat.XyComponentBuilder;
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
        String command = "/" + baseCommand + " " + displayName + " " + arguments;
        plr.spigot().sendMessage(
                new XyComponentBuilder(command, ChatColor.YELLOW)
                        .underlined(true)
                        .hintedCommand(command)
                        .append(" " + description, ChatColor.GOLD)
                        .underlined(false)
                        .create()
        );
    }

    @Override
    public String getPermission() {
        return permission;
    }
}
