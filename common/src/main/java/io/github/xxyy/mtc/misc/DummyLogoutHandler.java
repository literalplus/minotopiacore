/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

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
