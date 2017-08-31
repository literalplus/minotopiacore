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

package li.l1t.mtc.module.vote.reward.compound;

import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.CompoundReward;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Abstract base class for rewards that apply other rewards when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
abstract class AbstractCompoundReward implements CompoundReward, ConfigurationSerializable {
    public abstract Stream<Reward> findRewardsFor(Player player, Vote vote);

    @Override
    public void apply(Player player, Vote vote) {
        findRewardsFor(player, vote).forEach(reward -> reward.apply(player, vote));
    }
}
