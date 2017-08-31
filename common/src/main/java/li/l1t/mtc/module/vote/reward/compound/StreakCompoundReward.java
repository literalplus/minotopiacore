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
import li.l1t.common.exception.UserException;
import li.l1t.common.util.config.HashMapConfig;
import li.l1t.common.util.config.MapConfig;
import li.l1t.mtc.module.vote.api.Vote;
import li.l1t.mtc.module.vote.api.reward.Reward;
import li.l1t.mtc.module.vote.reward.Rewards;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Selects rewards based on how often a player has voted on consecutive days. This works by using the modulo
 * operator on the actual streak count.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2017-01-10
 */
@SerializableAs("mtc.vote.streak-of")
public class StreakCompoundReward extends AbstractCompoundReward {
    private static final String MODULO_BASE_PATH = "modulo-base";
    private static final String REWARDS_PATH = "rewards";
    private final Map<Number, Reward> rewardMap;
    private final int moduloBase;

    public StreakCompoundReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        moduloBase = config.findTyped(MODULO_BASE_PATH, Number.class)
                .orElseThrow(() -> new UserException("Missing modulo-base for StreakCompoundReward")).intValue();
        rewardMap = config.getMap(REWARDS_PATH, Number.class, Reward.class);
    }

    @Override
    public Stream<Reward> findRewardsFor(Player player, Vote vote) {
        int remainder = vote.getStreakLength() % moduloBase;
        Reward reward = rewardMap.get(remainder);
        return Rewards.stream(player, vote, reward);
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put(REWARDS_PATH, rewardMap)
                .put(MODULO_BASE_PATH, moduloBase)
                .build();
    }
}
