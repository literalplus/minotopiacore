package io.github.xxyy.mtc.misc;

import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.UUID;

/**
 * Dummy logout handler used when AntiLogout is disabled.
 * @author Janmm14
 */
public class DummyLogoutHandler implements AntiLogoutHandler {

    @Override
    public boolean isFighting(UUID uuid) {
        return false;
    }

    @Override
    public void setFighting(Player damaged, Player damager, Calendar cal) {
    }

    @Override
    public void clearFighters() {
    }
}
