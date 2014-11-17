package io.github.xxyy.mtc.misc;

import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.UUID;

/**
 * Handles punsishing players logging out while fighting
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 9.6.14
 */
public interface AntiLogoutHandler {
    boolean isFighting(UUID uuid);

    default void setFighting(Player plr, Player other, Calendar cal) { //Easier construction of dummies

    }

    default void clearFighters() { //Easier construction of dummies

    }
}
