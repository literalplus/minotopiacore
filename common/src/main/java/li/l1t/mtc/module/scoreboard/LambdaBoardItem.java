/*
 * MinoTopiaCore
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
