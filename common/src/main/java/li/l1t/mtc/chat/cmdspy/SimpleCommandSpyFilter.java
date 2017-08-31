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

package li.l1t.mtc.chat.cmdspy;

import org.bukkit.entity.Player;

import java.util.function.BiPredicate;

/**
 * A simple implementation of CommandSpyFilter.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 19.6.14
 */
public abstract class SimpleCommandSpyFilter implements CommandSpyFilter {
    private final BiPredicate<String, Player> predicate;

    public SimpleCommandSpyFilter(BiPredicate<String, Player> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean matches(String command, Player sender) {
        return predicate.test(command, sender);
    }

}
