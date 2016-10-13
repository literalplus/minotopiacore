/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance;

import li.l1t.mtc.module.putindance.api.board.Board;
import li.l1t.mtc.module.putindance.api.board.generator.AirStrategy;
import li.l1t.mtc.module.putindance.api.board.generator.BoardGenerator;
import li.l1t.mtc.module.putindance.api.board.generator.ColorSelector;
import li.l1t.mtc.module.putindance.api.game.Game;
import li.l1t.mtc.module.putindance.api.game.LayerSelector;
import li.l1t.mtc.module.putindance.api.game.TickStrategy;
import li.l1t.mtc.module.putindance.board.generator.DelegatingGenerationStrategy;
import li.l1t.mtc.module.putindance.board.generator.ListColorSelector;
import li.l1t.mtc.module.putindance.board.generator.RangeAirStrategy;
import li.l1t.mtc.module.putindance.board.generator.TransformerBoardGenerator;
import li.l1t.mtc.module.putindance.game.SimpleGame;
import li.l1t.mtc.module.putindance.game.layerselector.AnyLayerSelector;
import li.l1t.mtc.module.putindance.game.layerselector.TopMostLayerSelector;
import li.l1t.mtc.module.putindance.game.strategy.hardcore.TemporaryTickStrategy;
import li.l1t.mtc.module.putindance.game.strategy.putin.PermanentTickStrategy;

import java.util.function.Consumer;

/**
 * Wires PutinDance entities to the appropriate strategies defined in the configuration file.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-13
 */
class Wiring {
    private final PutinDanceConfig config;
    private final PutinDanceModule module;

    Wiring(PutinDanceConfig config, PutinDanceModule module) {
        this.config = config;
        this.module = module;
    }

    public BoardGenerator wireGenerator(Consumer<Board> callback) {
        DelegatingGenerationStrategy strategy = wireGenerationStrategy();
        TransformerBoardGenerator generator = new TransformerBoardGenerator(
                config.getFirstBoardBoundary(), config.getSecondBoardBoundary(),
                strategy, 200
        );
        generator.setCompletionCallback(callback);
        return generator;
    }

    public DelegatingGenerationStrategy wireGenerationStrategy() {
        return new DelegatingGenerationStrategy(wireAirStrategy(), wireColorSelector());
    }

    public AirStrategy wireAirStrategy() {
        RangeAirStrategy airStrategy = new RangeAirStrategy();
        airStrategy.setAirPercentRange(config.getMinAirPercent(), config.getMaxAirPercent());
        return airStrategy;
    }

    public ColorSelector wireColorSelector() {
        ListColorSelector colorSelector = new ListColorSelector();
        config.getValidColors().forEach(colorSelector::addValidColor);
        return colorSelector;
    }

    public Game wireGame(Board board) {
        SimpleGame game = new SimpleGame(board, wireTickStrategy());
        game.setSpawnLocation(config.getSpawnLocation());
        return game;
    }

    public TickStrategy wireTickStrategy() {
        if (config.isUseTemporaryRemovalStrategy()) {
            return wireTemporaryStrategy();
        } else {
            return wirePermanentStrategy();
        }
    }

    private PermanentTickStrategy wirePermanentStrategy() {
        return new PermanentTickStrategy(
                module.getPlugin(), config.getTickRemoveDelayTicks(), wireLayerSelector(), config.getBlocksPerTick()
        );
    }

    private TemporaryTickStrategy wireTemporaryStrategy() {
        return new TemporaryTickStrategy(
                module.getPlugin(), config.getTickRemoveDelayTicks(), config.getTickRevertDelayTicks(),
                config.getBlocksPerTick());
    }

    public LayerSelector wireLayerSelector() {
        if (config.isSelectOnlyTopMostLayers()) {
            return new TopMostLayerSelector();
        } else {
            return new AnyLayerSelector();
        }
    }
}
