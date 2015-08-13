package io.github.xxyy.mtc.misc;

import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.UUID;

/**
 * Dummy logout handler used when AntiLogout is disabled.
 */
public class DummyLogoutHandler implements AntiLogoutHandler {
    /**
     * @implNote returns always false
     */
    @Override
    public boolean isFighting(UUID uuid) {
        return false;
    }

    /**
     * @implNote does nothing
     */
    @Override
    public void setFighting(Player damaged, Player damager, Calendar cal) {
    }

    /**
     * @implNote does nothing
     */
    @Override
    public void clearFighters() {
    }
}
