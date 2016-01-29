package io.github.xxyy.mtc.module.shop.ui.text;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Handles a subcommand of a shop command.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2015-11-01
 */
public interface ShopAction {
    /**
     * Executes this action. Only arguments relevant to the action are passed, i.e. the action name is not passed.
     *
     * @param args the arguments for this action
     * @param plr  the player executing this action
     */
    void execute(String[] args, Player plr, String label);

    /**
     * @return the display name of this action
     */
    String getDisplayName();

    /**
     * Checks whether given action name matches this action, i.e. this action should be executed.
     *
     * @param actionName the action name
     * @return whether this action should be executed
     */
    boolean matches(String actionName);

    /**
     * Checks whether given action name could mean this action, i.e. we are not quite sure, but if we're the only one,
     * take it.
     *
     * @param actionName the action name
     * @return whether given action name could mean this action
     */
    boolean fuzzyMatches(String actionName);

    /**
     * Sends this action's help lines to a player.
     *
     * @param plr the player to send the lines to
     */
    void sendHelpLines(Player plr);

    /**
     * @return the amount of arguments this action needs at least
     */
    int getMinimumArguments();

    /**
     * @return the permission required to use this action, or null if none
     */
    @Nullable
    String getPermission();
}
