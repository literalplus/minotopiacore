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

import li.l1t.common.collections.Pair;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.logging.LogManager;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import li.l1t.mtc.module.vote.reward.Rewards;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Selects rewards based on weekday.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
@SerializableAs("mtc.vote.weekday-of")
public class WeekdayCompoundReward extends AbstractCompoundReward {
    private static final Logger LOGGER = LogManager.getLogger(WeekdayCompoundReward.class);
    private final Map<DayOfWeek, Reward> rewardMap = new EnumMap<>(DayOfWeek.class);

    public WeekdayCompoundReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        rewardMap.putAll(readRewardsFrom(config));
    }

    private Map<DayOfWeek, Reward> readRewardsFrom(MapConfig config) {
        /*
        We can't use the full MapConfig API here because Bukkit does not know how to save DayOfWeek enum constants
         */
        return Arrays.stream(DayOfWeek.values())
                .map(dow -> config.findTyped(dow.name(), Reward.class)
                        .map(val -> Pair.pairOf(dow, val)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @Override
    public Stream<Reward> findRewardsFor(Player player, Vote vote) {
        Reward todaysReward = rewardMap.get(DayOfWeek.from(LocalDateTime.now()));
        return Rewards.stream(player, vote, todaysReward);
    }

    @Override
    public Map<String, Object> serialize() {
        return rewardMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }
}
