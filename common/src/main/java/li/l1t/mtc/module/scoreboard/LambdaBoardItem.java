/*
 * Copyright (c) 2013-2016.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
 */

package li.l1t.mtc.module.scoreboard;

import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * A board item that computes its value from a Lambda expression.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-12-10
 */
public class LambdaBoardItem implements BoardItem {
    private final String identifier;
    private final String displayName;
    private final Function<Player, Object> valueFunction;

    public LambdaBoardItem(String identifier, String displayName, Function<Player, Object> valueFunction) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.valueFunction = valueFunction;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return true;
    }

    @Override
    public String getDisplayName(Player player) {
        return displayName;
    }

    @Override
    public String getValue(Player player) {
        return String.valueOf(valueFunction.apply(player));
    }

    @Override
    public void cleanUp(Player player) {
        //no-op
    }
}
