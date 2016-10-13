/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.api.game;

import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Board;
import org.bukkit.entity.Player;

/**
 * Represents a game of PutinDance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface Game {
    void addPlayer(Player player);

    boolean isOpen();

    void startGame();

    void openGame();

    void tick();

    boolean isTickable();

    void abortGame();

    Board getBoard();

    XyLocation getSpawnLocation();

    void setSpawnLocation(XyLocation spawnLocation);
}
