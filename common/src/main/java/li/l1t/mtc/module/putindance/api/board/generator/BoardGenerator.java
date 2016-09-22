/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.putindance.api.board.generator;

import li.l1t.mtc.module.putindance.api.board.Board;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * Takes care of generating a whole PutinDance board.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2016-09-21
 */
public interface BoardGenerator {
    GenerationStrategy getGenerationStrategy();

    void setCompletionCallback(Consumer<Board> callback);

    void startGeneration(Plugin plugin, long delayBetweenExecutions);
}
