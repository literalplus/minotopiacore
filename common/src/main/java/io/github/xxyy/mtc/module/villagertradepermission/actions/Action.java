/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package io.github.xxyy.mtc.module.villagertradepermission.actions;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for different actions to be executed when a player clicks on a villager
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public interface Action {

    void execute(@NotNull Player plr, @NotNull Villager selected);

    void sendActionInfo(@NotNull Player plr);

    String getShortDescription();
}
