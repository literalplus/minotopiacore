package io.github.xxyy.minotopiacore.misc;

import org.bukkit.entity.Player;

import java.util.Calendar;

/**
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public interface AntiLogoutHandler {
    boolean isFighting(String plrName);

    void setFighting(Player plr, Player other, Calendar cal);

    void clearFighters();
}
