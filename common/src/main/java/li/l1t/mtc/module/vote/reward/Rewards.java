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

package li.l1t.mtc.module.vote.reward;

import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.CompoundReward;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

/**
 * Provides static utility methods for dealing with rewards.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-11
 */
public class Rewards {
    private Rewards() {

    }

    public static Stream<Reward> stream(Player player, Vote vote, Reward... rewards) {
        Stream.Builder<Reward> builder = Stream.builder();
        for (Reward reward : rewards) {
            if(reward instanceof CompoundReward) {
                ((CompoundReward) reward).findRewardsFor(player, vote)
                        .forEach(builder::add);
            } else if(reward != null) {
               builder.add(reward);
            }
        }
        return builder.build();
    }
}
