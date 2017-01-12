/*
 * Copyright (c) 2013-2017.
 * This work is protected by international copyright laws and licensed
 * under the license terms which can be found at src/main/resources/LICENSE.txt
 * or alternatively obtained by sending an email to xxyy98+mtclicense@gmail.com.
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
    private final Map<Integer, Reward> rewardMap;
    private final int moduloBase;

    public StreakCompoundReward(Map<String, Object> source) {
        MapConfig config = HashMapConfig.of(source);
        moduloBase = config.findTyped(MODULO_BASE_PATH, int.class)
                .orElseThrow(() -> new UserException("Missing modulo-base for StreakCompoundReward"));
        rewardMap = config.getMap(REWARDS_PATH, int.class, Reward.class);
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
