/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.game;

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.TickStrategy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Stores metadata related to a game of PutinDance.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class SimpleGame implements Game {
    private final GameAnnouncer gameAnnouncer = new GameAnnouncer();
    private final Board board;
    private final TickStrategy tickStrategy;
    private XyLocation spawnLocation;
    private boolean open;

    public SimpleGame(Board board, TickStrategy tickStrategy) {
        this.board = Preconditions.checkNotNull(board, "board");
        this.tickStrategy = Preconditions.checkNotNull(tickStrategy, "tickStrategy");
    }

    @Override
    public void addPlayer(Player player) {
        player.teleport(spawnLocation);
        gameAnnouncer.announceGameJoin(player);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    public void openGame() {
        gameAnnouncer.announceGameOpen(Bukkit.getOnlinePlayers());
        open = true;
    }

    @Override
    public void tick() {
        tickStrategy.tick(this);
    }

    @Override
    public boolean isTickable() {
        return tickStrategy.isReady();
    }

    @Override
    public void startGame() {
        gameAnnouncer.announceGameStart(Bukkit.getOnlinePlayers());
        open = false;
    }

    public void abortGame() {
        gameAnnouncer.announceGameAbort(Bukkit.getOnlinePlayers());
        open = false;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public XyLocation getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public void setSpawnLocation(XyLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
