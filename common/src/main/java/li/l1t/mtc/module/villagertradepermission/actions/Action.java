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

package li.l1t.mtc.module.villagertradepermission.actions;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import javax.annotation.Nonnull;

/**
 * Interface for different actions to be executed when a player clicks on a villager
 *
 * @author <a href="https://janmm14.de">Janmm14</a>
 */
public interface Action {

    void execute(@Nonnull Player plr, @Nonnull Villager selected);

    void sendActionInfo(@Nonnull Player plr);

    String getShortDescription();
}
