/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import com.google.common.base.Preconditions;
import li.l1t.common.misc.XyLocation;
import li.l1t.common.util.CommandHelper;
import li.l1t.mtc.api.MTCPlugin;
import li.l1t.mtc.misc.ClearCacheBehaviour;
import li.l1t.mtc.module.ConfigurableMTCModule;
import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.generator.BoardGenerator;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.LayerSelector;
import li.l1t.mtc.module.putindance.board.generator.DelegatingGenerationStrategy;
import li.l1t.mtc.module.putindance.board.generator.ListColorSelector;
import li.l1t.mtc.module.putindance.board.generator.RangeAirStrategy;
import li.l1t.mtc.module.putindance.board.generator.TransformerBoardGenerator;
import li.l1t.mtc.module.putindance.game.SimpleGame;
import li.l1t.mtc.module.putindance.game.layerselector.AnyLayerSelector;
import li.l1t.mtc.module.putindance.game.layerselector.TopMostLayerSelector;
import li.l1t.mtc.module.putindance.game.strategy.putin.PermanentTickStrategy;

/**
 * A module that provides the PutinDance mini-game for events. PutinDance is based around a board
 * filled with different wool colors. Every time an admin executes /pd tick, some blocks are removed
 * from the board. The last player to be on a wool block wins. The board consists of multiple
 * layers.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-20
 */
public class PutinDanceModule extends ConfigurableMTCModule {
    public static final String NAME = "PutinDance";
    public static final String CHAT_PREFIX = "§3[§6§lPD§3] §3";
    public static final String ADMIN_PERMISSION = "mtc.putindance.admin";
    private final PutinDanceConfig config = new PutinDanceConfig();
    private WandHandler wandHandler;
    private Board currentBoard;
    private SimpleGame currentGame;

    protected PutinDanceModule() {
        super(NAME, "modules/events/putindance.cfg.yml", ClearCacheBehaviour.RELOAD_ON_FORCED, false);
    }

    @Override
    public void enable(MTCPlugin plugin) throws Exception {
        super.enable(plugin);

        wandHandler = new WandHandler(this);
        registerCommand(new PutinDanceCommand(this), "pd");
    }

    @Override
    protected void reloadImpl() {
        config.loadFrom(configuration);
    }

    @Override
    public void save() {
        config.saveTo(configuration);
        super.save();
    }

    public PutinDanceConfig getConfig() {
        return config;
    }

    public void setSpawnLocation(XyLocation spawnLocation) {
        config.setSpawnLocation(spawnLocation);
        if (hasGame()) {
            getCurrentGame().setSpawnLocation(spawnLocation);
        }
    }

    public void setBoardBoundaries(XyLocation first, XyLocation second) {
        config.setFirstBoardBoundary(first);
        config.setSecondBoardBoundary(second);
        save();
    }

    public WandHandler getWandHandler() {
        return wandHandler;
    }

    public boolean hasGame() {
        return currentGame != null;
    }

    public boolean hasOpenGame() {
        return currentGame != null && currentGame.isOpen();
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public BoardGenerator createGenerator() {
        RangeAirStrategy airStrategy = new RangeAirStrategy();
        airStrategy.setAirPercentRange(config.getMinAirPercent(), config.getMaxAirPercent());
        ListColorSelector colorSelector = new ListColorSelector();
        config.getValidColors().forEach(colorSelector::addValidColor);
        DelegatingGenerationStrategy strategy = new DelegatingGenerationStrategy(airStrategy, colorSelector);
        TransformerBoardGenerator generator = new TransformerBoardGenerator(
                config.getFirstBoardBoundary(), config.getSecondBoardBoundary(),
                strategy, 200
        );
        generator.setCompletionCallback(board -> {
            currentBoard = board;
            CommandHelper.broadcast(CHAT_PREFIX + "Spielfeld fertig generiert! §6/pd new", ADMIN_PERMISSION);
        });
        return generator;
    }

    public boolean hasBoard() {
        return currentBoard != null;
    }

    public void newGame() {
        Preconditions.checkState(!hasGame(), "there is already a game");
        Preconditions.checkState(hasBoard(), "there is no current board");
        PermanentTickStrategy strategy = createTickStrategy();
        currentGame = new SimpleGame(getCurrentBoard(), strategy);
        currentGame.setSpawnLocation(config.getSpawnLocation());
        currentGame.openGame();
    }

    private PermanentTickStrategy createTickStrategy() {
        return new PermanentTickStrategy(plugin, config.getTickRemoveDelayTicks(), createLayerSelector());
    }

    private LayerSelector createLayerSelector() {
        if (config.isSelectOnlyTopMostLayers()) {
            return new TopMostLayerSelector();
        } else {
            return new AnyLayerSelector();
        }
    }

    public void abortGame() {
        Preconditions.checkState(hasGame(), "no current game");
        currentGame.abortGame();
        currentGame = null;
    }
}
