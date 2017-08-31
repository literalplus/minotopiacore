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

import com.google.common.collect.ImmutableMap;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.common.util.math.WeightedDistribution;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A compound reward that randomly selects a reward to apply when applied.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-11
 */
@SerializableAs("mtc.vote.random-of")
public class RandomCompoundReward extends AbstractCompoundReward {
    private static final String REWARDS_PATH = "rewards";
    private final WeightedDistribution<Reward> distribution = new WeightedDistribution<>();

    public RandomCompoundReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        config.getCollection(REWARDS_PATH, Reward.class, Collectors.toList()).stream()
                .map(this::toWrapper)
                .forEach(wrapper -> distribution.put(wrapper, wrapper.getWeight()));
    }

    private WeightedRewardWrapper toWrapper(Reward reward) {
        if (reward instanceof WeightedRewardWrapper) {
            return (WeightedRewardWrapper) reward;
        } else {
            return new WeightedRewardWrapper(1D, reward);
        }
    }

    @Override
    public Stream<Reward> findRewardsFor(Player player, Vote vote) {
        return Stream.of(distribution.next());
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(REWARDS_PATH, distribution.probabilities().values())
                .build();
    }

}
